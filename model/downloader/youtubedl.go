/*
 @Author: ingbyr
*/

package downloader

import (
	"encoding/json"
	"github.com/ingbyr/vdm/model/goos"
	"regexp"
	"runtime"
)

const (
	FlagSimulateJson  = "-j"
	FlagNewLineOutput = "--newline"
)

var (
	youtubedl = &Youtubedl{
		baseDownloader: &baseDownloader{
			BaseInfo: &BaseInfo{
				Version:      "local",
				Name:         "youtube-dl",
				ExecutorPath: GetYoutubedlExecutorPath(),
			},
			CmdArgs: NewCmdArgs(),
		},
		regSpeed:    regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress: regexp.MustCompile("\\d+\\.?\\d*%"),
	}
)

func init() {
	_ = GetManager().Register(youtubedl)
}

func GetYoutubedlExecutorPath() string {
	switch runtime.GOOS {
	case goos.Windows:
		return ".\\runtime\\engine\\youtube-dl.exe"
	case goos.Linux:
		return "./runtime/engine/youtube-dl"
	case goos.MacOS:
		return "./runtime/engine/youtube-dl"
	default:
		return "not support os " + runtime.GOOS
	}
}

type Youtubedl struct {
	*baseDownloader
	regSpeed    *regexp.Regexp
	regProgress *regexp.Regexp
}

func (y *Youtubedl) FetchMediaInfo(task *Task) (MediaInfo, error) {
	y.Reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagSimulateJson)
	jsonData, err := y.Exec()
	if err != nil {
		return nil, err
	}
	var mediaInfo YoutubedlMediaInfo
	err = json.Unmarshal(jsonData, &mediaInfo)
	mediaInfo.Url = task.MediaUrl
	if err != nil {
		return nil, err
	}
	return &mediaInfo, nil
}

func (y *Youtubedl) Download(task *Task) {
	y.Reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagNewLineOutput)
	y.ExecAsync(task, y.UpdateTask)
}

func (y *Youtubedl) UpdateTask(task *Task, line string) {
	task.Progress = y.regProgress.FindString(line)
	task.Speed = y.regSpeed.FindString(line)
}

func (y *Youtubedl) Reset() {
	y.CmdArgs = NewCmdArgs()
}

type YoutubedlMediaInfo struct {
	Url       string                     `json:"url,omitempty"`
	Title     string                     `json:"title,omitempty"`
	FullTitle string                     `json:"fulltitle,omitempty"`
	Desc      string                     `json:"description,omitempty"`
	Formats   []youtubedlMediaJsonFormat `json:"formats,omitempty"`
}

func (y *YoutubedlMediaInfo) GetTitle() string {
	return y.Title
}

func (y *YoutubedlMediaInfo) GetFullTitle() string {
	return y.Title
}

func (y *YoutubedlMediaInfo) GetDesc() string {
	return y.Desc
}

func (y *YoutubedlMediaInfo) GetFormats() interface{} {
	return y.Formats
}

type youtubedlMediaJsonFormat struct {
	Format   string `json:"format,omitempty"`
	FormatId string `json:"format_id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"filesize,omitempty"`
}
