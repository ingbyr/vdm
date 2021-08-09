/*
 @Author: ingbyr
*/

package db

import (
	"fmt"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/uuid"
	"testing"
	"time"
)

func TestDb_Init(t *testing.T) {
	Setup()
	id := uuid.Instance.Generate().Int64()
	task := &model.DownloaderTask{
		ID:        id,
		CreatedAt: time.Now(),
		MediaBaseInfo: &model.MediaBaseInfo{
			Title: "title",
			Desc:  "desc",
		},
		DownloaderTaskConfig: &model.DownloaderTaskConfig{
			MediaUrl:    "media url",
			Downloader:  "downloader",
			StoragePath: "storage path",
			FormatId:    "42",
			FormatUrl:   "format url",
		},
		DownloaderTaskProgress: &model.DownloaderTaskProgress{
			Status:         model.TaskStatusRunning,
			DownloadedSize: "4242",
			Progress:       "42",
			Speed:          "42Mb/s",
		},
	}
	DB.Create(task)
}

func TestPage(t *testing.T) {
	Setup()
	tasks := make([]model.DownloaderTask, 0)
	DB.Find(&tasks)
	for _, task := range tasks {
		fmt.Printf("%v\n", task)
	}
}
