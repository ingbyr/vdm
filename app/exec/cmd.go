/*
 @Author: ingbyr
*/

package exec

import (
	"bufio"
	"bytes"
	"context"
	"errors"
	"github.com/ingbyr/vdm/pkg/logging"
	"io"
	"os/exec"
	"sync"
)

var log = logging.New("exec")

func Cmd(cmdName string, cmdArgs ...string) ([]byte, error) {
	cmd := exec.Command(cmdName, cmdArgs...)
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
	log.Debug("exec cmd finished", "output", stdout.String())
	return stdout.Bytes(), nil
}

type Context struct {
	context.Context
	Cancel    context.CancelFunc
	OnNewLine func(line string)
	OnError   func(err string)
	OnExit    func()
}

func CmdAsnyc(ctx Context, cmdName string, cmdArgs ...string) {
	cmd := exec.Command(cmdName, cmdArgs...)
	log.Debug("exec cmd args: %s", cmd.Args)
	stdOutput := make(chan string)
	errOutput := make(chan string)

	// callback loop
	go func() {
		defer ctx.OnExit()
		for {
			select {
			case out := <-stdOutput:
				log.Debug("stdout: %s", out)
				ctx.OnNewLine(out)
			case err := <-errOutput:
				log.Error("stderr: %s", err)
				ctx.OnError(err)
			case <-ctx.Done():
				log.Debug("finished execution")
				return
			}
		}
	}()

	// exec cmd
	go cmdAsnyc(ctx, cmd, stdOutput, errOutput)
}

func cmdAsnyc(ctx Context, cmd *exec.Cmd, stdoutC chan<- string, stderrC chan<- string) {
	defer close(stdoutC)
	defer close(stderrC)
	defer ctx.Cancel()

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

func readCmdPipe(wg *sync.WaitGroup, ctx Context, cmd *exec.Cmd, pipe io.Reader, output chan<- string) {
	defer wg.Done()
	scanner := bufio.NewScanner(pipe)
	for scanner.Scan() {
		select {
		case <-ctx.Done():
			if err := cmd.Process.Kill(); err != nil {
				log.Error("stop process", "error", err)
			}
			log.Debug("stop process", "pid", cmd.Process.Pid)
			break
		default:
			output <- scanner.Text()
		}
	}
}
