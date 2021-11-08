/*
 @Author: ingbyr
*/

package model

import (
	"bufio"
	"bytes"
	"context"
	"io"
	"os/exec"
	"sync"
)

const (
	HeartbeatDataTaskProgressGroup = "taskProgress"
	ProgressCompleted              = "100"
)

var cmdCtx context.Context

func SetupDownloader(ctx context.Context) {
	cmdCtx = ctx
}

// Dec downloader engine core
type Dec interface {
	GetName() string
	GetVersion() string
	GetExecutorPath() string
	Download(task *DTask)
	FetchMediaInfo(task *DTask) (*MediaInfo, error)
	IsValid() bool
	SetValid(valid bool)
}

type DecInfo struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executorPath"`
}

func (di *DecInfo) GetName() string {
	return di.Name
}

func (di *DecInfo) GetVersion() string {
	return di.Version
}

func (di *DecInfo) GetExecutorPath() string {
	return di.ExecutorPath
}

type decBase struct {
	*DecInfo
	CmdArgs
	Valid bool `json:"valid"`
}

func (d *decBase) Download(task *DTask) {
	panic("can't use base decBase")
}

func (d *decBase) FetchMediaInfo(task *DTask) (*MediaInfo, error) {
	panic("can't use base decBase")
}

func (d *decBase) IsValid() bool {
	return d.Valid
}

func (d *decBase) SetValid(valid bool) {
	d.Valid = valid
}

func (d *decBase) ExecCmd() ([]byte, error) {
	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	log.Debug("exec args: %v", cmd.Args)
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	cmd.Stderr = &stderr
	cmd.Stdout = &stdout
	err := cmd.Run()
	if err != nil {
		log.Error("exec error: %v", err)
		return stderr.Bytes(), err
	}
	log.Debug("output: %s", stdout.String())
	return stdout.Bytes(), nil
}

func (d *decBase) ExecCmdLong(
	data interface{},
	stepHandler func(data interface{}, line string),
	finalHandler func(data interface{}),
	errorHandler func(data interface{}, err string)) {

	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	log.Debug("exec cmd args: %s", cmd.Args)
	stdOutput := make(chan string)
	errOutput := make(chan string)
	cmdSubCtx, cancel := context.WithCancel(cmdCtx)

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
			case <-cmdSubCtx.Done():
				log.Debug("finished execution")
				return
			}
		}
	}()

	// exec cmd
	go execCmdLong(cmdSubCtx, cancel, cmd, stdOutput, errOutput)
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
