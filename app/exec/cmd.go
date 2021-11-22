/*
 @Author: ingbyr
*/

package exec

import (
	"bufio"
	"bytes"
	"context"
	"fmt"
	"github.com/ingbyr/vdm/mode"
	"github.com/ingbyr/vdm/pkg/logging"
	"io"
	"os/exec"
	"sync"
)

var log = logging.New("exec")

func Cmd(ctx context.Context, args *Args) ([]byte, error) {
	cmd := exec.CommandContext(ctx, args.executor, args.Args()...)
	log.Debugw("exec cmd", "cmd", cmd.Args)
	if mode.DisableCmd {
		return nil, nil
	}
	var stderr bytes.Buffer
	var stdout bytes.Buffer
	cmd.Stderr = &stderr
	cmd.Stdout = &stdout
	if err := cmd.Run(); err != nil {
		log.Errorw("exec cmd failed", "error", err.Error(), "stderr", stderr.String())
		return nil, fmt.Errorf(stderr.String(), err)
	}
	log.Debugw("exec cmd finished", "output", stdout.String())
	return stdout.Bytes(), nil
}

type Callback struct {
	OnNewLine func(line string)
	OnError   func(err string)
	OnExit    func()
}

func CmdAsnyc(ctx context.Context, cancel context.CancelFunc, callback Callback, cmdArgs *Args) {
	cmd := exec.CommandContext(ctx, cmdArgs.executor, cmdArgs.Args()...)
	log.Debugw("exec cmd", "cmd", cmd.Args)
	if mode.DisableCmd {
		return
	}

	stdOutput := make(chan string)
	errOutput := make(chan string)

	// callback loop
	go func() {
		defer callback.OnExit()
		for {
			select {
			case out := <-stdOutput:
				log.Debugw("process running", "output", out)
				callback.OnNewLine(out)
			case err := <-errOutput:
				log.Error("process error", "output", err)
				callback.OnError(err)
			case <-ctx.Done():
				log.Debug("finished process")
				return
			}
		}
	}()

	// exec cmd
	go cmdAsnyc(ctx, cancel, cmd, stdOutput, errOutput)
}

func cmdAsnyc(ctx context.Context, cancel context.CancelFunc, cmd *exec.Cmd, stdoutC chan<- string, stderrC chan<- string) {
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
	if err = cmd.Start(); err != nil {
		stderrC <- err.Error()
		return
	}
	// read stdout and stderr output
	var wg sync.WaitGroup
	wg.Add(2)
	go readCmdPipe(ctx, &wg, cmd, stdoutPipe, stdoutC)
	go readCmdPipe(ctx, &wg, cmd, stderrPipe, stderrC)
	wg.Wait()
}

func readCmdPipe(ctx context.Context, wg *sync.WaitGroup, cmd *exec.Cmd, pipe io.Reader, output chan<- string) {
	defer wg.Done()
	scanner := bufio.NewScanner(pipe)
	for scanner.Scan() {
		select {
		case <-ctx.Done():
			if err := cmd.Process.Kill(); err != nil {
				log.Errorw("stop process", "error", err)
			}
			log.Debugw("stop process", "pid", cmd.Process.Pid)
			break
		default:
			output <- scanner.Text()
		}
	}
}
