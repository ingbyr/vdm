/*
 @Author: ingbyr
*/

package engine

import (
	"github.com/ingbyr/vdm/model/media"
	"github.com/ingbyr/vdm/pkg/logging"
)

const (
	HeartbeatDataTaskProgressGroup = "taskProgress"
	ProgressCompleted              = "100"
)

var log = logging.New("engine")

// Engine is media downloader
type Engine interface {
	GetName() string
	GetVersion() string
	GetExecutorPath() string
	Download(task *DTask)
	FetchMediaInfo(task *DTask) (*media.Info, error)
	IsValid() bool
	SetValid(valid bool)
}

type info struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executorPath"`
	Valid        bool   `json:"valid"`
}

func (e *info) GetName() string {
	return e.Name
}

func (e *info) GetVersion() string {
	return e.Version
}

func (e *info) GetExecutorPath() string {
	return e.ExecutorPath
}

func (e *info) Download(task *DTask) {
	panic("can't use base decBase")
}

func (e *info) FetchMediaInfo(task *DTask) (*media.Info, error) {
	panic("can't use base decBase")
}

func (e *info) IsValid() bool {
	return e.Valid
}

func (e *info) SetValid(valid bool) {
	e.Valid = valid
}
