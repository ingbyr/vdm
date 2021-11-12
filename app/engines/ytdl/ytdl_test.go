/*
 @Author: ingbyr
*/

package ytdl

import (
	"context"
	"fmt"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/setting"
	"path"
	"testing"
	"time"
)

const (
	mediaUrl = "https://www.bilibili.com/video/BV12L4y1q7iL"
	baseDir  = "../../../"
)

func init() {
	_ytdl.ExecutorPath = path.Join(baseDir, _ytdl.ExecutorPath)
}

func TestYoutubedl_FetchMediaInfo(t *testing.T) {
	ctx, cancel := context.WithTimeout(context.TODO(), 10*time.Second)
	mTask := &task.MTask{
		Engine:   name,
		MediaUrl: mediaUrl,
		Ctx:      ctx,
		Cancel:   cancel,
	}
	data, err := _ytdl.FetchMediaInfo(mTask)
	fmt.Printf("err: %v, data: %+v\n", err, data)
}

func TestYoutubedl_FetchMediaInfo_Timeout(t *testing.T) {
	ctx, cancel := context.WithTimeout(context.TODO(), 10*time.Millisecond)
	mTask := &task.MTask{
		Engine:   name,
		MediaUrl: mediaUrl,
		Ctx:      ctx,
		Cancel:   cancel,
	}
	data, err := _ytdl.FetchMediaInfo(mTask)
	fmt.Printf("err: %v, data: %+v\n", err, data)
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
	dTask := task.NewDTask(nil)
	handler := _ytdl.taskUpdateHandler(dTask)
	for i, o := range output {
		handler(o)
		fmt.Printf("%d %+v\n", i, dTask.Progress)
	}
}

func TestYoutubedl_GenerateStoragePath(t *testing.T) {
	fmt.Println(_ytdl.getStoragePath("./runtime"))
	fmt.Println(_ytdl.getStoragePath("./runtime/"))
}

func TestYoutubedl_Download(t *testing.T) {
	dTask := task.NewDTask(&task.DTaskOpt{
		MediaUrl:    mediaUrl,
		Engine:      _ytdl.Name,
		StoragePath: path.Join(baseDir, setting.DirRuntime),
		FormatId:    "",
	})
	fmt.Printf("%+v\n", dTask.DTaskOpt)
	_ytdl.DownloadMedia(dTask)
}
