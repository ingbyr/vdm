/*
 @Author: ingbyr
*/

package downloader

import (
	"bufio"
	"bytes"
	"github.com/ingbyr/vdm/pkg/logging"
	"os/exec"
	"strings"
)

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

type BaseDownloader interface {
	GetName() string
	GetVersion() string
	GetExecutorPath() string
	Download(task *Task)
	FetchMediaInfo(task *Task) (MediaInfo, error)
}

type BaseInfo struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executor_path"`
}

func (b *BaseInfo) GetName() string {
	return b.Name
}

func (b *BaseInfo) GetVersion() string {
	return b.Version
}

func (b *BaseInfo) GetExecutorPath() string {
	return b.ExecutorPath
}

type baseDownloader struct {
	*BaseInfo
	CmdArgs
}

func (b *baseDownloader) Exec() ([]byte, error) {
	command := exec.Command(b.ExecutorPath, b.toCmdStrSlice()...)
	logging.Debug("exec args: %v", command.Args)
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	command.Stderr = &stderr
	command.Stdout = &stdout
	err := command.Run()
	if err != nil {
		logging.Error("exec error %v", stderr)
		return stderr.Bytes(), err
	}
	logging.Debug("output: %s", stdout.String())
	return stdout.Bytes(), nil
}

func (b *baseDownloader) ExecAsync(task *Task, updater func(task *Task, line string)) {
	task.Status = TaskRunning
	cmd := exec.Command(b.ExecutorPath, b.toCmdStrSlice()...)
	logging.Debug("exec args: %v\n", cmd.Args)
	output := make(chan string)
	TaskCache.collectTask(task)
	go b.exec(cmd, output)
	go func() {
		// parse download output and update task
		for out := range output {
			logging.Debug("output: %s", out)
			updater(task, out)
		}
		if strings.HasPrefix("100", task.Progress) {
			task.Status = TaskFinished
		} else {
			task.Status = TaskPaused
		}
		TaskCache.clearTask(task.Id)
	}()
}

func (b *baseDownloader) exec(cmd *exec.Cmd, output chan<- string) {
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
		case <-DCtx.Done():
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
