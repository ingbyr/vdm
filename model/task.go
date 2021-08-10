/*
 @Author: ingbyr
*/

package model

const (
	TaskStatusCreated = iota
	TaskStatusCompleted
	TaskStatusError
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
	Progress string `json:"progress" db:"progress"`
	Speed    string `json:"speed" db:"speed"`
}

type DownloaderTask struct {
	Model
	Status int `json:"status" form:"status"`
	*MediaBaseInfo
	*DownloaderTaskConfig
	*DownloaderTaskProgress `gorm:"-"`
}

func NewDownloaderTask(taskConfig *DownloaderTaskConfig) *DownloaderTask {
	return &DownloaderTask{
		Model:                  NewModel(),
		Status:                 TaskStatusCreated,
		DownloaderTaskConfig:   taskConfig,
		DownloaderTaskProgress: &DownloaderTaskProgress{},
	}
}
