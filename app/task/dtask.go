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
	*DTaskOpt
	*Progress
	*media.Media
	Ctx    context.Context    `json:"-" gorm:"-"`
	Cancel context.CancelFunc `json:"-" gorm:"-"`
}

type DTaskOpt struct {
	MediaUrl    string `json:"mediaUrl" gorm:"mediaUrl" form:"mediaUrl"`
	Engine      string `json:"engine" gorm:"engine" form:"engine"`
	StoragePath string `json:"storagePath" gorm:"storagePath" form:"storagePath"`
	FormatId    string `json:"formatId" gorm:"formatId" form:"formatId"`
}

type Progress struct {
	Percent   string `json:"progress" gorm:"percent"`
	Speed     string `json:"speed" gorm:"speed"`
	Status    status `json:"status" gorm:"status"`
	StatusMsg string `json:"status_msg" gorm:"status_msg"`
}

func NewDTask(taskOpt *DTaskOpt) *DTask {
	return &DTask{
		Model:    db.NewModel(),
		DTaskOpt: taskOpt,
		Progress: &Progress{
			Status: Created,
		},
	}
}
