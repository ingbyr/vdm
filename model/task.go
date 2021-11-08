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

// DTaskConfig download task config
type DTaskConfig struct {
	MediaUrl    string `json:"mediaUrl"`
	Downloader  string `json:"decBase" form:"decBase"`
	StoragePath string `json:"storagePath" form:"storagePath"`
	FormatId    string `json:"formatId"`
	FormatUrl   string `json:"formatUrl"`
}

// DTaskProgress downloader task progress
type DTaskProgress struct {
	Progress string `json:"progress" db:"progress"`
	Speed    string `json:"speed" db:"speed"`
}

// DTask downloader task
type DTask struct {
	Model
	Status int `json:"status" form:"status"`
	*MediaBaseInfo
	*DTaskConfig
	*DTaskProgress `gorm:"-"`
}

func NewDTask(taskConfig *DTaskConfig) *DTask {
	return &DTask{
		Model:         NewModel(),
		Status:        TaskStatusCreated,
		DTaskConfig:   taskConfig,
		DTaskProgress: &DTaskProgress{},
	}
}
