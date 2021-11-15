/*
 @Author: ingbyr
*/

package task

import (
	"context"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/pkg/store"
)

// DTask is a media downloading task
type DTask struct {
	*store.Model
	// TODO decouple
	Media       *media.Media       `json:"media" gorm:"embedded;embeddedPrefix:media_"`
	Engine      string             `json:"engine" gorm:"engine" form:"engine" binding:"required"`
	ExtArgs     string             `json:"extArgs" gorm:"ext_args"`
	StoragePath string             `json:"storagePath" gorm:"storage_path" form:"storagePath"`
	// TODO embed this
	Progress    *Progress          `json:"progress" gorm:"embedded;embeddedPrefix:progress_"`
	Status      status             `json:"status" gorm:"status"`
	Ctx         context.Context    `json:"-" gorm:"-"`
	Cancel      context.CancelFunc `json:"-" gorm:"-"`
}

type Progress struct {
	Percent   string `json:"progress" gorm:"percent"`
	Speed     string `json:"speed" gorm:"speed"`
	StatusMsg string `json:"status_msg" gorm:"status_msg"`
}

func NewDTask() *DTask {
	return &DTask{
		Model:    store.NewModel(),
		Progress: &Progress{},
	}
}

func AddDTask(d *DTask) {

}

func GetDTasks() {

}

func GetSameDTasks(o *DTask) {
	//  d.Media == o.Media &&
	//	d.Engine == o.Engine &&
	//	d.ExtArgs == o.ExtArgs &&
	//	d.StoragePath == o.StoragePath
}
