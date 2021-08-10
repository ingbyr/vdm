/*
 @Author: ingbyr
*/

package model

import (
	"bufio"
	"bytes"
	"context"
	"github.com/ingbyr/vdm/pkg/logging"
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

type Downloader interface {
	GetName() string
	GetVersion() string
	GetExecutorPath() string
	Download(task *DownloaderTask)
	FetchMediaInfo(task *DownloaderTask) (*MediaInfo, uint)
	SetValid(valid bool)
}

type DownloaderInfo struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executorPath"`
}

func (di *DownloaderInfo) GetName() string {
	return di.Name
}

func (di *DownloaderInfo) GetVersion() string {
	return di.Version
}

func (di *DownloaderInfo) GetExecutorPath() string {
	return di.ExecutorPath
}

type downloader struct {
	*DownloaderInfo
	CmdArgs
	Valid  bool `json:"valid"`
	Enable bool `json:"enable"`
}

func (d *downloader) Download(task *DownloaderTask) {
	panic("can't use base downloader")
}

func (d *downloader) FetchMediaInfo(task *DownloaderTask) (*MediaInfo, uint) {
	panic("can't use base downloader")
}

func (d *downloader) SetValid(valid bool) {
	d.Valid = valid
}

func (d *downloader) ExecCmd() ([]byte, error) {
	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	logging.Debug("exec args: %v", cmd.Args)
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	cmd.Stderr = &stderr
	cmd.Stdout = &stdout
	err := cmd.Run()
	if err != nil {
		logging.Error("exec error %v", err)
		return stderr.Bytes(), err
	}
	logging.Debug("output: %s", stdout.String())
	return stdout.Bytes(), nil
}

func (d *downloader) ExecCmdLong(
	data interface{},
	stepHandler func(data interface{}, line string),
	finalHandler func(data interface{}),
	errorHandler func(data interface{}, err string)) {

	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	logging.Debug("exec cmd args: %s", cmd.Args)
	stdOutput := make(chan string)
	errOutput := make(chan string)
	cmdSubCtx, cancel := context.WithCancel(cmdCtx)

	// read output
	go func() {
		defer finalHandler(data)
		for {
			select {
			case out := <-stdOutput:
				logging.Debug("stdout: %s", out)
				stepHandler(data, out)
			case err := <-errOutput:
				logging.Error("stderr: %s", err)
				errorHandler(data, err)
			case <-cmdSubCtx.Done():
				logging.Debug("finished to exec cmd")
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
				logging.Error("failed to stop process: %v", err)
			}
			logging.Debug("stop process: %v", cmd.Process.Pid)
			break
		default:
			output <- scanner.Text()
		}
	}
}
