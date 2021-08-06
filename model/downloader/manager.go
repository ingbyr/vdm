/*
 @Author: ingbyr
*/

package downloader

import (
	"context"
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/pkg/logging"
	"os/exec"
	"sync"
)

var once sync.Once
var DCtx context.Context

func Init(ctx context.Context) {
	once.Do(func() {
		DCtx = ctx
	})
}

var manager = &Manager{
	Downloaders: make(map[string]Downloader),
}

func GetManager() *Manager {
	return manager
}

type Manager struct {
	Downloaders map[string]Downloader `json:"downloaders,omitempty"`
}

func (m *Manager) Register(downloader Downloader) error {
	var err error
	if _, err = exec.LookPath(downloader.GetExecutorPath()); err != nil {
		downloader.SetValid(false)
		logging.Warn("downloader '%s' is not valid because '%s' not found", downloader.GetName(), downloader.GetExecutorPath())
	}
	m.Downloaders[downloader.GetName()] = downloader
	return err
}

func (m *Manager) Enabled(downloader Downloader) bool {
	_, ok := m.Downloaders[downloader.GetName()]
	return ok
}

func (m *Manager) Download(task *Task) error {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("downloader '%s' not found or is disabled", task.Downloader))
	}
	downloader.Download(task)
	return nil
}

func (m *Manager) FetchMediaInfo(task *Task) (*MediaInfo, error) {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return nil, errors.New(fmt.Sprintf("downloader '%s' not found or is disabled", task.Downloader))
	}
	return downloader.FetchMediaInfo(task)
}
