/*
 @Author: ingbyr
*/

package model

import (
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/ws"
	"os/exec"
)

var DownloaderManager = &manager{
	Downloaders:   make(map[string]Downloader),
	TaskProgress:  make(map[int64]*DownloaderTaskProgress),
	ManagerConfig: &ManagerConfig{EnableWsSender: false},
}

type manager struct {
	Downloaders  map[string]Downloader             `json:"downloaders,omitempty"`
	TaskProgress map[int64]*DownloaderTaskProgress `json:"progress,omitempty"`
	*ManagerConfig
}

type ManagerConfig struct {
	EnableWsSender bool
}

func (m *manager) setup(mc *ManagerConfig) {
	if mc.EnableWsSender {
		ws.UpdateHeartbeatData("taskProgress", m.TaskProgress)
	}
}

func (m *manager) Register(downloader Downloader) {
	if _, err := exec.LookPath(downloader.GetExecutorPath()); err != nil {
		downloader.SetValid(false)
		logging.Warn("downloader '%s' is not valid because '%s' not found", downloader.GetName(), downloader.GetExecutorPath())
	}
	m.Downloaders[downloader.GetName()] = downloader
}

func (m *manager) Enabled(downloader Downloader) bool {
	_, ok := m.Downloaders[downloader.GetName()]
	return ok
}

func (m *manager) Download(task *DownloaderTask) error {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("downloader '%s' not found or is disabled", task.Downloader))
	}
	downloader.Download(task)
	return nil
}

func (m *manager) FetchMediaInfo(task *DownloaderTask) (*MediaInfo, error) {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return nil, errors.New(fmt.Sprintf("downloader '%s' not found or is disabled", task.Downloader))
	}
	return downloader.FetchMediaInfo(task)
}

func (m *manager) UpdateTaskProgress(task *DownloaderTask) {
	m.TaskProgress[task.ID] = task.DownloaderTaskProgress
}

func (m *manager) RemoveTaskProgress(task *DownloaderTask) {
	delete(m.TaskProgress, task.ID)
}
