/*
 @Author: ingbyr
*/

package downloader

import (
	"fmt"
	"testing"
)

const mediaUrl = "https://www.bilibili.com/video/BV1q64y147nh"

func TestYoutubedl_FetchMediaSimulateJson(t *testing.T) {
	task := NewTask(&TaskConfig{MediaUrl: mediaUrl})
	data, err := youtubedl.FetchMediaInfo(task)
	if err != nil {
		fmt.Printf("Error %v\n", err)
	}
	fmt.Printf("%+v\n", data)
}

func TestYoutubedl_Download(t *testing.T) {
	task := NewTask(&TaskConfig{MediaUrl: mediaUrl})
	youtubedl.Download(task)
}

func TestYoutubedl_ParseDownloadOutput(t *testing.T) {
	output := []string{
		"1q64y147nh: Downloading webpage",
		"[download]   0.0% of 41.53MiB at 352.93KiB/s ETA 02:00",
		"[download]   1.2% of 41.53MiB at 1012.79KiB/s ETA 00:41",
		"[download]   33.33% of 41.53MiB at 11.79MB/s ETA 00:41",
		"[download] 100% of 41.53MiB in 00:48",
	}
	for i, o := range output {
		fmt.Printf("%v) progress %v, speed %v \n", i,
			youtubedl.regProgress.FindString(o),
			youtubedl.regSpeed.FindString(o))
	}
}
