/*
 @Author: ingbyr
*/

package db

import (
	"github.com/ingbyr/vdm/model/downloader"
	"github.com/ingbyr/vdm/pkg/uuid"
	"testing"
	"time"
)

func TestDb_Init(t *testing.T) {
	Setup("tmp.db")
	id := uuid.Instance.Generate().Int64()
	task := &downloader.Task{
		ID:        id,
		CreatedAt: time.Now(),
		Status:    2,
		MediaBaseInfo: &downloader.MediaBaseInfo{
			Title: "title",
			Desc:  "desc",
		},
		TaskConfig: &downloader.TaskConfig{
			TaskId:      id,
			MediaUrl:    "media url",
			Downloader:  "downloader",
			StoragePath: "storage path",
			FormatId:    "42",
			FormatUrl:   "format url",
		},
		TaskProgress: &downloader.TaskProgress{
			DownloadedSize: "4242",
			Progress:       "42",
			Speed:          "42Mb/s",
		},
	}
	Db.Create(task)
	Db.Create(task.TaskConfig)
}
