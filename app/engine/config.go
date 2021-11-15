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

var _ Engine = (*Config)(nil)

type Config struct {
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

func (b *Config) GetConfig() *Config {
	return b
}

func (b *Config) FetchMediaInfo(mTask *task.MTask) (*media.Media, error) {
	panic("implement me")
}

func (b *Config) DownloadMedia(dTask *task.DTask) error {
	panic("implement me")
}

func (b *Config) Broadcast(dTask *task.DTask) {
	jsonData, _ := json.Marshal(dTask)
	if store.DB == nil {
		panic("db not loaded")
	}
	store.DB.Save(dTask)


	ws.Broadcast(jsonData)
}
