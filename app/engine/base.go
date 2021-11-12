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

var _ Engine = (*Base)(nil)

type Base struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executorPath"`
	Valid        bool   `json:"valid"`
}

func (b *Base) GetBase() *Base {
	return b
}

func (b *Base) FetchMediaInfo(mTask *task.MTask) (*media.Media, error) {
	panic("implement me")
}

func (b *Base) DownloadMedia(dTask *task.DTask) error {
	panic("implement me")
}

func (b *Base) Broadcast(dTask *task.DTask) {
	jsonData, _ := json.Marshal(dTask)
	if db.DB == nil {
		panic("db not loaded")
	}
	db.DB.Save(dTask)
	ws.Broadcast(jsonData)
}
