/*
 @Author: ingbyr
*/

package engine

import (
	"bufio"
	"bytes"
	"context"
	"errors"
	"github.com/ingbyr/vdm/model/media"
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/logging"
	"io"
	"os/exec"
	"sync"
)

const (
	HeartbeatDataTaskProgressGroup = "taskProgress"
	ProgressCompleted              = "100"
)

var (
	log    = logging.New("engine")
	ctx    context.Context
	cancel context.CancelFunc
)

func Setup(globalCtx context.Context, globalCancel context.CancelFunc) {
	ctx = globalCtx
	cancel = globalCancel
}

// Engine is media downloader
type Engine interface {
	GetName() string
	GetVersion() string
	GetExecutorPath() string
	Download(task *task.DTask)
	FetchMediaInfo(task *task.MTask) (*media.Info, error)
	IsValid() bool
	SetValid(valid bool)
}

type engine struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executorPath"`
	Valid        bool   `json:"valid"`
	*opts
}

func (e *engine) GetName() string {
	return e.Name
}

func (e *engine) GetVersion() string {
	return e.Version
}

func (e *engine) GetExecutorPath() string {
	return e.ExecutorPath
}

func (e *engine) Download(task *task.DTask) {
	panic("can't use base decBase")
}

func (e *engine) FetchMediaInfo(task *task.MTask) (*media.Info, error) {
	panic("can't use base decBase")
}

func (e *engine) IsValid() bool {
	return e.Valid
}

func (e *engine) SetValid(valid bool) {
	e.Valid = valid
}

func (e *engine) ExecCmd() ([]byte, error) {
	cmd := exec.Command(e.ExecutorPath, e.toCmdStrSlice()...)
	log.Debugw("exec cmd", "cmd", cmd.Args)
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	cmd.Stderr = &stderr
	cmd.Stdout = &stdout
	err := cmd.Run()
	if err != nil {
		errMsg := err.Error() + ": " + stderr.String()
		log.Error("exec cmd error", "error", errMsg)
		return nil, errors.New(errMsg)
	}
	log.Debug("output: %s", stdout.String())
	return stdout.Bytes(), nil
}

type StepHandler = func(data interface{}, line string)
type FinalHandler = func(data interface{})
type ErrorHandler = func(data interface{}, err string)

func (e *engine) ExecCmdLong(data interface{}, stepHandler StepHandler, finalHandler FinalHandler, errorHandler ErrorHandler) {
	cmd := exec.Command(e.ExecutorPath, e.toCmdStrSlice()...)
	log.Debug("exec cmd args: %s", cmd.Args)
	stdOutput := make(chan string)
	errOutput := make(chan string)

	// read output
	go func() {
		defer finalHandler(data)
		for {
			select {
			case out := <-stdOutput:
				log.Debug("stdout: %s", out)
				stepHandler(data, out)
			case err := <-errOutput:
				log.Error("stderr: %s", err)
				errorHandler(data, err)
			case <-ctx.Done():
				log.Debug("finished execution")
				return
			}
		}
	}()

	// exec cmd
	go execCmdLong(ctx, cancel, cmd, stdOutput, errOutput)
}

func execCmdLong(ctx context.Context, cancel context.CancelFunc, cmd *exec.Cmd, stdoutC chan<- string, stderrC chan<- string) {
	defer close(stdoutC)
	defer close(stderrC)
	defer cancel()
	// prepare cmd pipe
	stdoutPipe, err := cmd.StdoutPipe()
	if err != nil {
		stderrC <- err.Error()
		return
	}
	stderrPipe, err := cmd.StderrPipe()
	if err != nil {
		stderrC <- err.Error()
		return
	}
	// exec cmd
	if err := cmd.Start(); err != nil {
		stderrC <- err.Error()
		return
	}
	// read stdout and stderr output
	var wg sync.WaitGroup
	wg.Add(2)
	go readCmdPipe(&wg, ctx, cmd, stdoutPipe, stdoutC)
	go readCmdPipe(&wg, ctx, cmd, stderrPipe, stderrC)
	wg.Wait()
}

func readCmdPipe(wg *sync.WaitGroup, ctx context.Context, cmd *exec.Cmd, pipe io.Reader, output chan<- string) {
	defer wg.Done()
	scanner := bufio.NewScanner(pipe)
	for scanner.Scan() {
		select {
		case <-ctx.Done():
			if err := cmd.Process.Kill(); err != nil {
				log.Error("failed to stop process: %v", err)
			}
			log.Debug("stop process: %v", cmd.Process.Pid)
			break
		default:
			output <- scanner.Text()
		}
	}
}
