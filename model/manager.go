/*
 @Author: ingbyr
*/

package model

import (
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/pkg/logging"
	"os/exec"
)

var DownloaderManager = &downloaderManager{
	Downloaders:  make(map[string]Downloader),
}

type downloaderManager struct {
	Downloaders  map[string]Downloader             `json:"downloaders,omitempty"`
}

func (m *downloaderManager) Register(downloader Downloader) {
	if _, err := exec.LookPath(downloader.GetExecutorPath()); err != nil {
		downloader.SetValid(false)
		logging.Warn("downloader '%s' is not valid because '%s' not found", downloader.GetName(), downloader.GetExecutorPath())
	}
	m.Downloaders[downloader.GetName()] = downloader
}

func (m *downloaderManager) Enabled(downloader Downloader) bool {
	_, ok := m.Downloaders[downloader.GetName()]
	return ok
}

func (m *downloaderManager) Download(task *DownloaderTask) error {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("downloader '%s' not found or is disabled", task.Downloader))
	}
	downloader.Download(task)
	return nil
}

func (m *downloaderManager) FetchMediaInfo(task *DownloaderTask) (*MediaInfo, error) {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return nil, errors.New(fmt.Sprintf("downloader '%s' not found or is disabled", task.Downloader))
	}
	return downloader.FetchMediaInfo(task)
}
