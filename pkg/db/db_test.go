/*
 @Author: ingbyr
*/

package db

import (
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
		Status:    2,
		MediaBaseInfo: &model.MediaBaseInfo{
			Title: "title",
			Desc:  "desc",
		},
		DownloaderTaskConfig: &model.DownloaderTaskConfig{
			TaskId:      id,
			MediaUrl:    "media url",
			Downloader:  "downloader",
			StoragePath: "storage path",
			FormatId:    "42",
			FormatUrl:   "format url",
		},
		DownloaderTaskProgress: &model.DownloaderTaskProgress{
			DownloadedSize: "4242",
			Progress:       "42",
			Speed:          "42Mb/s",
		},
	}
	Db.Create(task)
	Db.Create(task.DownloaderTaskConfig)
}
