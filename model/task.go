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
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"downloader"`
	StoragePath string `json:"storagePath"`
	FormatId    string `json:"formatId"`
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
	*DownloaderTaskConfig `gorm:"embedded"`
	*DownloaderTaskProgress `gorm:"embedded"`
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
