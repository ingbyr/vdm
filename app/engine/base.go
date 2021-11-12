/*
 @Author: ingbyr
*/

package engine

import (
	"encoding/json"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/ws"
)

var _ Engine = (*Info)(nil)

type Info struct {
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

func (b *Info) GetInfo() *Info {
	return b
}

func (b *Info) FetchMediaInfo(mTask *task.MTask) (*media.Media, error) {
	panic("implement me")
}

func (b *Info) DownloadMedia(dTask *task.DTask) error {
	panic("implement me")
}

func (b *Info) Broadcast(dTask *task.DTask) {
	jsonData, _ := json.Marshal(dTask)
	if db.DB == nil {
		panic("db not loaded")
	}
	db.DB.Save(dTask)
	ws.Broadcast(jsonData)
}
