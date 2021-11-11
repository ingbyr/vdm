/*
 @Author: ingbyr
*/

package task

import (
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
	db.Model
	*DTaskOpt
	*Progress
	*media.Media
}

type DTaskOpt struct {
	MediaUrl    string `json:"mediaUrl" db:"mediaUrl" form:"mediaUrl"`
	Engine      string `json:"engine" db:"engine" form:"engine"`
	StoragePath string `json:"storagePath" db:"storagePath" form:"storagePath"`
	FormatId    string `json:"formatId" db:"formatId" form:"formatId"`
}

type Progress struct {
	Percent   string `json:"progress" db:"percent"`
	Speed     string `json:"speed" db:"speed"`
	Status    status `json:"status" db:"status"`
	StatusMsg string `json:"status_msg" db:"status_msg"`
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
