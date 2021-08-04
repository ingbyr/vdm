/*
 @Author: ingbyr
*/

package downloader

import (
	"github.com/ingbyr/vdm/pkg/uuid"
	"github.com/ingbyr/vdm/pkg/ws"
	"time"
)

const (
	TaskCreated = iota
	TaskRunning
	TaskPaused
	TaskFinished
)

func init() {
	TaskSender = &taskCache{
		Progress: make(map[int64]*TaskProgress),
	}
	ws.UpdateHeartbeatData("task", TaskSender)
}

type TaskConfig struct {
	MediaUrl     string `json:"media_url"`
	Downloader   string `json:"downloader"`
	StoragePath  string `json:"storage_path,omitempty"`
	FormatId     string `json:"format_id,omitempty"`
}

type TaskProgress struct {
	DownloadedSize string `json:"downloaded_size,omitempty"`
	Progress       string `json:"progress,omitempty"`
	Speed          string `json:"speed,omitempty"`
}

type Task struct {
	Id        int64
	CreatedAt time.Time
	Title     string
	Status    int
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

type taskCache struct {
	// Key is task id
	Progress map[int64]*TaskProgress `json:"progress"`
}

var TaskSender = &taskCache{
	Progress: make(map[int64]*TaskProgress),
}

func (tm *taskCache) collect(task *Task) {
	tm.Progress[task.Id] = task.TaskProgress
}

func (tm *taskCache) remove(id int64) {
	delete(tm.Progress, id)
}
