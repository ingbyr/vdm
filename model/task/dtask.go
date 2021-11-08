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

// Config download task config
type Config struct {
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader" form:"downloader"`
	StoragePath string `json:"storagePath" form:"storagePath"`
	FormatId    string `json:"formatId"`
	FormatUrl   string `json:"formatUrl"`
}

// Progress downloader task progress
type Progress struct {
	Percent string `json:"progress" db:"progress"`
	Speed   string `json:"speed" db:"speed"`
}

// Task downloader task
type Task struct {
	model.Model
	Status int `json:"status" form:"status"`
	media.Base
	Config
	*Progress `gorm:"-"`
}

func NewTask(taskConfig Config) *Task {
	return &Task{
		Model:    model.NewModel(),
		Status:   StatusCreated,
		Config:   taskConfig,
		Progress: &Progress{},
	}
}
