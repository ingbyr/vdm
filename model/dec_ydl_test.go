/*
 @Author: ingbyr
*/

package model

import (
	"context"
	"fmt"
	"testing"
)

const mediaUrl = "https://www.bilibili.com/video/BV1q64y147nh"



func TestYoutubedl_FetchMediaSimulateJson(t *testing.T) {
	task := NewDTask(&DTaskConfig{MediaUrl: mediaUrl})
	data, err := decYdl.FetchMediaInfo(task)
	fmt.Printf("err: %v, data: %+v\n", err, data)
}

func TestYoutubedl_Download(t *testing.T) {
	SetupDownloader(context.Background())
	task := NewDTask(&DTaskConfig{MediaUrl: mediaUrl})
	decYdl.Download(task)
}

func TestYoutubedl_ParseDownloadOutput(t *testing.T) {
	output := []string{
		"1q64y147nh: Downloading webpage",
		"[execAsync]   0.0% of 41.53MiB at 352.93KiB/s ETA 02:00",
		"[execAsync]   1.2% of 41.53MiB at 1012.79KiB/s ETA 00:41",
		"[execAsync]   33.33% of 41.53MiB at 11.79MB/s ETA 00:41",
		"[execAsync] 100% of 41.53MiB in 00:48",
		"[execAsync] ./runtime/demo.mp4 has already been downloaded and merged",
	}
	task := NewDTask(&DTaskConfig{})
	for i, o := range output {
		decYdl.downloaderTaskUpdateHandler(task, o)
		fmt.Printf("%d %+v\n", i, task.DTaskProgress)
	}
}

func TestYoutubedl_GenerateStoragePath(t *testing.T) {
	fmt.Println(decYdl.getStoragePath("./runtime"))
}
