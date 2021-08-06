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

func setupTaskSender() {
	TaskSender = &taskSender{
		Progress: make(map[int64]*TaskProgress),
	}
	ws.UpdateHeartbeatData("task", TaskSender)
}

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

type taskSender struct {
	// Key is task id
	Progress map[int64]*TaskProgress `json:"progress"`
}

var TaskSender = &taskSender{
	Progress: make(map[int64]*TaskProgress),
}

func (tm *taskSender) collect(task *Task) {
	tm.Progress[task.Id] = task.TaskProgress
}

func (tm *taskSender) remove(id int64) {
	delete(tm.Progress, id)
}
