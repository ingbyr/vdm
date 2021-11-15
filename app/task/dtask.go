/*
 @Author: ingbyr
*/

package task

import (
	"context"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/pkg/db"
)

type status = int

const (
	Created status = iota
	Downloading
	Merging
	Paused
	Completed
	Failed
)

// DTask is a media downloading task
type DTask struct {
	*db.Model
	Media       *media.Media       `json:"media" gorm:"embedded"`
	Engine      string             `json:"engine" gorm:"engine" form:"engine"`
	StoragePath string             `json:"storagePath" gorm:"storagePath" form:"storagePath"`
	Progress    *Progress          `json:"progress" gorm:"embedded"`
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
		Model:    db.NewModel(),
		Progress: &Progress{},
	}
}
