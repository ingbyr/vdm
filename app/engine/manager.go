/*
 @Author: ingbyr
*/

package engine

import (
	"context"
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"os/exec"
	"time"
)

var (
	m = &manager{
		Engines: make(map[string]Engine),
	}
)

type manager struct {
	Engines map[string]Engine `json:"engines,omitempty"`
}

func Engines() map[string]Engine {
	return m.Engines
}

func Register(engine Engine) {
	if _, err := exec.LookPath(engine.GetBase().ExecutorPath); err != nil {
		engine.GetBase().Valid = false
		log.Warnf("engine '%s' is not valid because '%s' not found", engine.GetBase().Name, engine.GetBase().ExecutorPath)
	}
	m.Engines[engine.GetBase().Name] = engine
}

func Enabled(engine Engine) bool {
	_, ok := m.Engines[engine.GetBase().Name]
	return ok
}

func FetchMediaInfo(mTask *task.MTask) (*media.Media, error) {
	engine, ok := m.Engines[mTask.Engine]
	if !ok {
		return nil, errors.New("not found Base")
	}
	mTask.Ctx, mTask.Cancel = context.WithTimeout(ctx, 10*time.Second)
	return engine.FetchMediaInfo(mTask)
}

func DownloadMedia(task *task.DTask) error {
	engine, ok := m.Engines[task.Engine]
	if !ok {
		return fmt.Errorf("engine '%s' not found", task.Engine)
	}
	task.Ctx, task.Cancel = context.WithCancel(ctx)
	engine.DownloadMedia(task)
	return nil
}
