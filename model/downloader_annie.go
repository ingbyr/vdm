/*
 @Author: ingbyr
*/

package model

var (
	downloaderAnnie = &Annie{
		downloader: &downloader{
			DownloaderInfo: &DownloaderInfo{
				Version:      "not exist",
				Name:         "annie",
				ExecutorPath: "not exist",
			},
			CmdArgs: NewCmdArgs(),
			Valid:   true,
		},
	}
)

func init() {
	DownloaderManager.Register(downloaderAnnie)
}

type Annie struct {
	*downloader
}
