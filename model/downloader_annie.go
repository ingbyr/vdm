/*
 @Author: ingbyr
*/

package model

var (
	downloaderAnnie = &DownloaderAnnie{
		downloader: &downloader{
			DownloaderInfo: &DownloaderInfo{
				Version:      "not exist",
				Name:         "annie",
				ExecutorPath: "not exist",
			},
			CmdArgs: NewCmdArgs(),
			Valid:   true,
			Enable:  true,
		},
	}
)

func init() {
	DownloaderManager.Register(downloaderAnnie)
}

type DownloaderAnnie struct {
	*downloader
}
