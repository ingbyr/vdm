/*
 @Author: ingbyr
*/

package model

import (
	"bufio"
	"bytes"
	"context"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/ws"
	"os/exec"
)

const (
	ProgressCompleted = "100"
)

var ctx context.Context

func SetupDownloader(_ctx context.Context) {
	ctx = _ctx
	DownloaderManager.setup(&ManagerConfig{EnableWsSender: true})
}

type Downloader interface {
	GetName() string
	GetVersion() string
	GetExecutorPath() string
	Download(task *DownloaderTask)
	FetchMediaInfo(task *DownloaderTask) (*MediaInfo, error)
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

func (d *downloader) FetchMediaInfo(task *DownloaderTask) (*MediaInfo, error) {
	panic("can't use base downloader")
}

func (d *downloader) SetValid(valid bool) {
	d.Valid = valid
}

func (d *downloader) exec() ([]byte, error) {
	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	logging.Debug("_exec args: %v", cmd.Args)
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	cmd.Stderr = &stderr
	cmd.Stdout = &stdout
	err := cmd.Run()
	if err != nil {
		logging.Error("_exec error %v", stderr)
		return stderr.Bytes(), err
	}
	logging.Debug("output: %s", stdout.String())
	return stdout.Bytes(), nil
}

func (d *downloader) execAsync(task *DownloaderTask, updater func(task *DownloaderTask, line string)) {
	task.Status = TaskStatusRunning
	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	logging.Debug("exec args: %v", cmd.Args)
	DownloaderManager.UpdateTaskProgress(task)
	output := make(chan string)
	_ctx, cancel := context.WithCancel(ctx)
	go d._exec(_ctx, cmd, output)
	go func() {
		defer cancel()
		// parse download output and update task
		for out := range output {
			logging.Debug("output: %s", out)
			updater(task, out)
		}
		if task.Status != TaskStatusCompleted {
			task.Status = TaskStatusPaused
		}
		// avoid to miss status notification
		ws.InvokeHeartbeat()
		DownloaderManager.RemoveTaskProgress(task)
	}()
}

func (d *downloader) _exec(_ctx context.Context, cmd *exec.Cmd, output chan<- string) {
	defer close(output)
	pipe, err := cmd.StdoutPipe()
	if err != nil {
		output <- err.Error()
		panic(err)
	}
	if err := cmd.Start(); err != nil {
		output <- err.Error()
		return
	}
	scanner := bufio.NewScanner(pipe)
	for scanner.Scan() {
		select {
		case <-_ctx.Done():
			if err := cmd.Process.Kill(); err != nil {
				logging.Error("failed to stop process: %v", err)
			}
			logging.Debug("stop process: %v", cmd.Process.Pid)
			break
		default:
			m := scanner.Text()
			output <- m
		}
	}
}
