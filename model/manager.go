/*
 @Author: ingbyr
*/

package model

import (
	"encoding/json"
	"errors"
	"fmt"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/logging"
	"os/exec"
)

var DownloaderManager = &downloaderManager{
	Downloaders: make(map[string]Downloader),
}

type downloaderManager struct {
	Downloaders map[string]Downloader `json:"downloaders,omitempty"`
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

func (m *downloaderManager) FetchMediaInfo(task *DownloaderTask) (*MediaInfo, uint) {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return nil, e.DownloaderUnavailable
	}
	res, err := downloader.FetchMediaInfo(task)
	if err != nil {
		logging.Error(err.Error())
		switch err.(type) {
		case *exec.ExitError:
			return nil, e.DownloaderUnknown
		case *json.UnmarshalTypeError, *json.InvalidUnmarshalError,
			*json.UnsupportedTypeError, *json.UnsupportedValueError,
			*json.SyntaxError:
			return nil, e.JsonNonDeserializable
		}
	}
	return res, e.Ok
}
