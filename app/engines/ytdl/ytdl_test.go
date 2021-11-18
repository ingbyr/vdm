/*
 @Author: ingbyr
*/

package ytdl

import (
	"context"
	"fmt"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/setting"
	"github.com/ingbyr/vdm/pkg/store"
	"path"
	"testing"
	"time"
)

const (
	mediaUrl = "https://www.bilibili.com/video/BV12L4y1q7iL"
	baseDir  = "../../../"
)

func init() {
	_ytdl.Executor = path.Join(baseDir, _ytdl.Executor)
	store.Setup()
	store.DB.AutoMigrate(task.DTask{})
}

func TestYoutubedl_FetchMediaInfo(t *testing.T) {
	ctx, cancel := context.WithTimeout(context.TODO(), 10*time.Second)
	mTask := &task.MTask{
		Engine:   name,
		MediaUrl: mediaUrl,
		Ctx:      ctx,
		Cancel:   cancel,
	}
	data, err := _ytdl.FetchMediaFormats(mTask)
	if err != nil {
		t.Fatal(err)
	}
	fmt.Printf("%+v\n", data)
}

func TestYoutubedl_FetchMediaInfo_Timeout(t *testing.T) {
	ctx, cancel := context.WithTimeout(context.TODO(), 10*time.Millisecond)
	mtask := &task.MTask{
		Engine:   name,
		MediaUrl: mediaUrl,
		Ctx:      ctx,
		Cancel:   cancel,
	}
	data, err := _ytdl.FetchMediaFormats(mtask)
	if err != nil {
		t.Fatal(err)
	}
	fmt.Printf("%+v\n", data)
}

func TestYoutubedl_ParseDownloadOutput(t *testing.T) {
	output := []string{
		"1q64y147nh: Downloading webpage",
		"[exec]   0.0% of 41.53MiB at 352.93KiB/s ETA 02:00",
		"[exec]   1.2% of 41.53MiB at 1012.79KiB/s ETA 00:41",
		"[exec]   33.33% of 41.53MiB at 11.79MB/s ETA 00:41",
		"[exec] 100% of 41.53MiB in 00:48",
		"[exec] ./runtime/demo.mp4 has already been downloaded and merged",
	}
	dtask := &task.DTask{}
	handler := _ytdl.taskUpdateHandler(dtask)
	for i, o := range output {
		handler(o)
		fmt.Printf("%d %+v\n", i, dtask.Progress)
	}
}

func TestYoutubedl_GenerateStoragePath(t *testing.T) {
	fmt.Println(_ytdl.getStoragePath("./runtime"))
	fmt.Println(_ytdl.getStoragePath("./runtime/"))
}

func TestYoutubedl_DownloadMedia(t *testing.T) {
	ctx, cancel := context.WithCancel(context.TODO())
	dtask := &task.DTask{
		Model:       store.NewModel(),
		Engine:      _ytdl.Name,
		StoragePath: path.Join(baseDir, setting.DirRuntime),
		FormatId:    "0",
		Progress: task.Progress{
			Status: task.Created,
		},
		Media: &media.Info{
			Title: "test title",
			Desc:  "test desc",
			Url:   mediaUrl,
		},
		Ctx:    ctx,
		Cancel: cancel,
	}
	if err := _ytdl.DownloadMedia(dtask); err != nil {
		t.Fatal(err)
	}
	<-dtask.Ctx.Done()
}
