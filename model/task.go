/*
 @Author: ingbyr
*/

package model

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

type DownloaderTaskConfig struct {
	TaskId      int64  `json:"taskId" gorm:"primaryKey"`
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader"`
	StoragePath string `json:"storagePath,omitempty"`
	FormatId    string `json:"formatId,omitempty"`
	FormatUrl   string `json:"formatUrl,omitempty"`
}

type DownloaderTaskProgress struct {
	DownloadedSize string `json:"downloadedSize,omitempty" db:"download_size"`
	Progress       string `json:"progress,omitempty" db:"progress"`
	Speed          string `json:"speed,omitempty" db:"speed"`
}

type DownloaderTask struct {
	ID             int64     `json:"id"`
	CreatedAt      time.Time `json:"createdAt"`
	Status         int       `json:"status"`
	*MediaBaseInfo `gorm:"embedded"`
	*DownloaderTaskConfig
	*DownloaderTaskProgress
}

func NewDownloaderTask(taskConfig *DownloaderTaskConfig) *DownloaderTask {
	return &DownloaderTask{
		ID:                     uuid.Instance.Generate().Int64(),
		CreatedAt:              time.Now(),
		Status:                 TaskCreated,
		DownloaderTaskConfig:   taskConfig,
		DownloaderTaskProgress: &DownloaderTaskProgress{},
	}
}
