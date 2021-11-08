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

var (
	log = logging.New("model")
	DecManager = &decManager{
		Downloaders: make(map[string]Dec),
	}
)

type decManager struct {
	Downloaders map[string]Dec `json:"downloaders,omitempty"`
}

func (m *decManager) Register(downloader Dec) {
	if _, err := exec.LookPath(downloader.GetExecutorPath()); err != nil {
		downloader.SetValid(false)
		log.Warnf("engine '%s' is not valid because '%s' not found", downloader.GetName(), downloader.GetExecutorPath())
	}
	m.Downloaders[downloader.GetName()] = downloader
}

func (m *decManager) Enabled(downloader Dec) bool {
	_, ok := m.Downloaders[downloader.GetName()]
	return ok
}

func (m *decManager) Download(task *DTask) error {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return errors.New(fmt.Sprintf("decBase '%s' not found or is disabled", task.Downloader))
	}
	downloader.Download(task)
	return nil
}

func (m *decManager) FetchMediaInfo(task *DTask) (*MediaInfo, uint) {
	downloader, ok := m.Downloaders[task.Downloader]
	if !ok {
		return nil, e.DownloaderUnavailable
	}
	res, err := downloader.FetchMediaInfo(task)
	if err != nil {
		log.Error(err.Error())
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
