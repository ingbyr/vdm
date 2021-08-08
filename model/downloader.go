/*
 @Author: ingbyr
*/

package model

import (
	"bufio"
	"bytes"
	"context"
	"github.com/ingbyr/vdm/pkg/logging"
	"os/exec"
	"strings"
)

const (
	ProgressCompleted = "100"
)

var ctx context.Context

func SetupDownloader(_ctx context.Context) {
	ctx = _ctx
	DownloaderManager.setup(&ManagerConfig{EnableWsSender: true})
}

type CmdArgs struct {
	args  map[string]string
	flags []string
}

func NewCmdArgs() CmdArgs {
	return CmdArgs{
		args:  make(map[string]string),
		flags: make([]string, 0),
	}
}

func (c *CmdArgs) addFlag(flag string) {
	c.flags = append(c.flags, flag)
}

func (c *CmdArgs) addFlagValue(flag string, value string) {
	c.addFlag(flag)
	c.args[flag] = value
}

func (c *CmdArgs) toCmdStrSlice() []string {
	return strings.Split(c.toCmdStr(), " ")
}

func (c *CmdArgs) toCmdStr() string {
	sp := " "
	var sb strings.Builder
	for _, f := range c.flags {
		if sb.Len() != 0 {
			sb.WriteString(sp)
		}
		sb.WriteString(f)
		if v, ok := c.args[f]; ok {
			sb.WriteString(sp)
			sb.WriteString(v)
		}
	}
	return sb.String()
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
	ExecutorPath string `json:"executor_path"`
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

func (d *downloader) SetValid(valid bool) {
	d.Valid = valid
}

func (d *downloader) Exec() ([]byte, error) {
	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	logging.Debug("exec args: %v", cmd.Args)
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	cmd.Stderr = &stderr
	cmd.Stdout = &stdout
	err := cmd.Run()
	if err != nil {
		logging.Error("exec error %v", stderr)
		return stderr.Bytes(), err
	}
	logging.Debug("output: %s", stdout.String())
	return stdout.Bytes(), nil
}

func (d *downloader) ExecAsync(task *DownloaderTask, updater func(task *DownloaderTask, line string)) {
	task.Status = TaskStatusRunning
	cmd := exec.Command(d.ExecutorPath, d.toCmdStrSlice()...)
	logging.Debug("exec args: %v", cmd.Args)
	DownloaderManager.UpdateTaskProgress(task)
	output := make(chan string)
	ctx, cancel := context.WithCancel(ctx)
	go d.exec(ctx, cmd, output)
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
		DownloaderManager.RemoveTaskProgress(task)
	}()
}

func (d *downloader) exec(ctx context.Context, cmd *exec.Cmd, output chan<- string) {
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
		case <-ctx.Done():
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
