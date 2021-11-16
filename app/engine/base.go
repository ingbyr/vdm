/*
 @Author: ingbyr
*/

package engine

import (
	"encoding/json"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/store"
	"github.com/ingbyr/vdm/pkg/ws"
)

var _ Engine = (*Base)(nil)

type Base struct {
	// Name is a common name for engine
	Name string `json:"name"`

	// Version is engine executor binary version
	Version string `json:"version"`

	// Executor is engine executor binary path
	Executor string `json:"executor"`

	// Enable if enable is false, the engine will not register to manager
	Enable bool `json:"enable"`

	// Valid is false if engine path not existed or not an executable binary
	Valid bool `json:"valid"`
}

func (b *Base) GetBase() *Base {
	return b
}

func (b *Base) FetchMediaFormats(mTask *task.MTask) (*media.Formats, error) {
	panic("implement me")
}

func (b *Base) DownloadMedia(dTask *task.DTask) error {
	panic("implement me")
}

func (b *Base) Broadcast(dTask *task.DTask) {
	dtaskProgress, _ := json.Marshal(dTask.Progress)
	if store.DB == nil {
		panic("db not loaded")
	}
	// TODO use task.SaveDTask
	store.DB.Save(dTask)
	ws.Broadcast(dtaskProgress)
}
