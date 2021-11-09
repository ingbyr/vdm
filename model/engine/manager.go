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
	if _, err := exec.LookPath(engine.Config().ExecutorPath); err != nil {
		engine.Config().Valid = false
		log.Warnf("config '%s' is not Valid because '%s' not found", engine.Config().Name, engine.Config().ExecutorPath)
	}
	Manager.Engines[engine.Config().Name] = engine
}

func (m *manager) Enabled(engine Engine) bool {
	_, ok := m.Engines[engine.Config().Name]
	return ok
}

func (m *manager) Download(task *task.DTask) error {
	engine, ok := m.Engines[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("decBase '%s' not found or is disabled", task.Downloader))
	}
	engine.DownloadMedia(task)
	return nil
}

func (m *manager) FetchMediaInfo(task *task.MTask) (*media.Info, error) {
	engine, ok := m.Engines[task.Engine]
	if !ok {
		return nil, errors.New("not found config")
	}
	return engine.FetchMediaInfo(task)
}
