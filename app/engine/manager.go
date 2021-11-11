/*
 @Author: ingbyr
*/

package engine

import (
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"os/exec"
)

var (
	Manager = &manager{
		Engines: make(map[string]Engine),
	}
)

type manager struct {
	Engines map[string]Engine `json:"engines,omitempty"`
}

func Register(engine Engine) {
	if _, err := exec.LookPath(engine.GetBase().ExecutorPath); err != nil {
		engine.GetBase().Valid = false
		log.Warnf("engine '%s' is not valid because '%s' not found", engine.GetBase().Name, engine.GetBase().ExecutorPath)
	}
	Manager.Engines[engine.GetBase().Name] = engine
}

func (m *manager) Enabled(engine Engine) bool {
	_, ok := m.Engines[engine.GetBase().Name]
	return ok
}

func (m *manager) Download(task *task.DTask) error {
	engine, ok := m.Engines[task.Engine]
	if !ok {
		return errors.New(fmt.Sprintf("engine '%s' not found or is disabled", task.Engine))
	}
	engine.DownloadMedia(task)
	return nil
}

func (m *manager) FetchMediaInfo(task *task.MTask) (*media.Media, error) {
	engine, ok := m.Engines[task.Engine]
	if !ok {
		return nil, errors.New("not found Base")
	}
	return engine.FetchMediaInfo(task)
}
