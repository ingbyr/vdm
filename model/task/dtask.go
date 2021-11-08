/*
 @Author: ingbyr
*/

package task

import (
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/model/media"
)

const (
	StatusCreated = iota
	StatusCompleted
	StatusError
	StatusPaused
	StatusRunning
)

// DTask is a media downloading task
type DTask struct {
	model.Model
	Status int `json:"status" form:"status"`
	media.Base
	Config
	*Progress `gorm:"-"`
}

func NewDTask(taskConfig Config) *DTask {
	return &DTask{
		Model:    model.NewModel(),
		Status:   StatusCreated,
		Config:   taskConfig,
		Progress: &Progress{},
	}
}

// Config for dtask
type Config struct {
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader" form:"downloader"`
	StoragePath string `json:"storagePath" form:"storagePath"`
	FormatId    string `json:"formatId"`
	FormatUrl   string `json:"formatUrl"`
}

// Progress for dtask
type Progress struct {
	Percent string `json:"progress" db:"progress"`
	Speed   string `json:"speed" db:"speed"`
}


