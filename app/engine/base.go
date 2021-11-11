/*
 @Author: ingbyr
*/

package engine

import (
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
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

func (b *Base) FetchMediaInfo(task *task.MTask) (*media.Media, error) {
	panic("implement me")
}

func (b *Base) DownloadMedia(task *task.DTask) {
	panic("implement me")
}
