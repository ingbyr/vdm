/*
 @Author: ingbyr
*/

package model

import (
	"github.com/ingbyr/vdm/pkg/uuid"
	"strconv"
	"time"
)

const (
	TaskStatusCreated = iota
	TaskStatusCompleted
	TaskStatusPaused
	TaskStatusRunning
)

type DownloaderTaskConfig struct {
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader"`
	StoragePath string `json:"storagePath"`
	FormatId    string `json:"formatId"`
	FormatUrl   string `json:"formatUrl"`
}

type DownloaderTaskProgress struct {
	Status         int    `json:"status"`
	Progress       string `json:"progress" db:"progress"`
	Speed          string `json:"speed" db:"speed"`
}

type DownloaderTask struct {
	ID        int64     `json:"id"`
	CreatedAt time.Time `json:"createdAt"`
	UpdatedAt time.Time
	*MediaBaseInfo
	*DownloaderTaskConfig
	*DownloaderTaskProgress
}

func NewDownloaderTask(taskConfig *DownloaderTaskConfig) *DownloaderTask {
	return &DownloaderTask{
		ID:                   uuid.Instance.Generate().Int64(),
		CreatedAt:            time.Now(),
		DownloaderTaskConfig: taskConfig,
		DownloaderTaskProgress: &DownloaderTaskProgress{
			Status: TaskStatusCreated,
		},
	}
}

func (d *DownloaderTask) getStrID() string {
	return strconv.FormatInt(d.ID, 10)
}
