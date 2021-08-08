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
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader"`
	StoragePath string `json:"storagePath,omitempty"`
	FormatId    string `json:"formatId,omitempty"`
	FormatUrl   string `json:"formatUrl,omitempty"`
}

type TaskProgress struct {
	DownloadedSize string `json:"downloadedSize,omitempty"`
	Progress       string `json:"progress,omitempty"`
	Speed          string `json:"speed,omitempty"`
}

type Task struct {
	Id        int64 	`json:"id"`
	CreatedAt time.Time `json:"createdAt"`
	Status    int
	*MediaBaseInfo
	*TaskConfig
	*TaskProgress
}

func NewTask(taskConfig *TaskConfig) *Task {
	return &Task{
		Id:           uuid.Instance.Generate().Int64(),
		CreatedAt:    time.Now(),
		Status:       TaskCreated,
		TaskConfig:   taskConfig,
		TaskProgress: &TaskProgress{},
	}
}
