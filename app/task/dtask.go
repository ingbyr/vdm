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
	Media       *media.Selection   `json:"media" gorm:"embedded;embeddedPrefix:media_"`
	Engine      string             `json:"engine" gorm:"engine" form:"engine" binding:"required"`
	ExtArgs     string             `json:"extArgs" gorm:"ext_args"`
	StoragePath string             `json:"storagePath" gorm:"storage_path" form:"storagePath"`
	Progress    *Progress          `json:"progress" gorm:"embedded;embeddedPrefix:progress_"`
	Ctx         context.Context    `json:"-" gorm:"-"`
	Cancel      context.CancelFunc `json:"-" gorm:"-"`
}

type Progress struct {
	Status    status `json:"status" gorm:"status"`
	CmdOutput string `json:"cmdOutput" gorm:"cmd_output"`
	Percent   string `json:"progress" gorm:"percent"`
	Speed     string `json:"speed" gorm:"speed"`
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
