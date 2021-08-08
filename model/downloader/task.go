/*
 @Author: ingbyr
*/

package downloader

import (
	"github.com/ingbyr/vdm/pkg/uuid"
	"time"
)

const (
	TaskCreated = iota
	TaskRunning
	TaskPaused
	TaskFinished
)

type TaskConfig struct {
	TaskId      int64  `json:"taskId" gorm:"primaryKey"`
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader"`
	StoragePath string `json:"storagePath,omitempty"`
	FormatId    string `json:"formatId,omitempty"`
	FormatUrl   string `json:"formatUrl,omitempty"`
}

type TaskProgress struct {
	DownloadedSize string `json:"downloadedSize,omitempty" db:"download_size"`
	Progress       string `json:"progress,omitempty" db:"progress"`
	Speed          string `json:"speed,omitempty" db:"speed"`
}

type Task struct {
	ID             int64     `json:"id"`
	CreatedAt      time.Time `json:"createdAt"`
	Status         int       `json:"status"`
	*MediaBaseInfo `gorm:"embedded"`
	*TaskConfig
	*TaskProgress
}

func NewTask(taskConfig *TaskConfig) *Task {
	return &Task{
		ID:           uuid.Instance.Generate().Int64(),
		CreatedAt:    time.Now(),
		Status:       TaskCreated,
		TaskConfig:   taskConfig,
		TaskProgress: &TaskProgress{},
	}
}
