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
	EnabledDownloader:    make(map[string]BaseDownloader),
	NotEnabledDownloader: make(map[string]BaseDownloader),
}

func GetManager() *Manager {
	return manager
}

type Manager struct {
	EnabledDownloader    map[string]BaseDownloader `json:"enabled_downloader,omitempty"`
	NotEnabledDownloader map[string]BaseDownloader `json:"not_enabled_downloader,omitempty"`
}

func (m *Manager) Register(downloader BaseDownloader) error {
	if _, err := exec.LookPath(downloader.GetExecutorPath()); err != nil {
		m.NotEnabledDownloader[downloader.GetName()] = downloader
		delete(m.EnabledDownloader, downloader.GetName())
		logging.Warn("can't register %s because '%s' not found", downloader.GetName(), downloader.GetExecutorPath())
		return err
	}

	m.EnabledDownloader[downloader.GetName()] = downloader
	delete(m.NotEnabledDownloader, downloader.GetName())
	return nil
}

func (m *Manager) Enabled(downloader BaseDownloader) bool {
	_, ok := m.EnabledDownloader[downloader.GetName()]
	return ok
}

func (m *Manager) Download(task *Task) error {
	downloader, ok := m.EnabledDownloader[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("Downloader '%s' not found or is disabled", task.Downloader))
	}
	downloader.Download(task)
	return nil
}

func (m *Manager) FetchMediaInfo(task *Task) (MediaInfo, error) {
	downloader, ok := m.EnabledDownloader[task.Downloader]
	if !ok {
		return nil, errors.New(fmt.Sprintf("Downloader '%s' not found or is disabled", task.Downloader))
	}
	return downloader.FetchMediaInfo(task)
}
