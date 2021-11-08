/*
 @Author: ingbyr
*/

package engine

import (
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/model/media"
	"github.com/ingbyr/vdm/model/task"
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

func register(engine Engine) {
	if _, err := exec.LookPath(engine.GetExecutorPath()); err != nil {
		engine.SetValid(false)
		log.Warnf("engine '%s' is not valid because '%s' not found", engine.GetName(), engine.GetExecutorPath())
	}
	Manager.Engines[engine.GetName()] = engine
}

func (m *manager) Enabled(engine Engine) bool {
	_, ok := m.Engines[engine.GetName()]
	return ok
}

func (m *manager) Download(task *task.DTask) error {
	engine, ok := m.Engines[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("decBase '%s' not found or is disabled", task.Downloader))
	}
	engine.Download(task)
	return nil
}

func (m *manager) FetchMediaInfo(task *task.MTask) (*media.Info, error) {
	engine, ok := m.Engines[task.Engine]
	if !ok {
		return nil, errors.New("not found engine")
	}
	return engine.FetchMediaInfo(task)
}
