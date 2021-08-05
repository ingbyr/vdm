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
		downloader: &downloader{
			Info: &Info{
				Version:      "local",
				Name:         "youtube-dl",
				ExecutorPath: GetYoutubedlExecutorPath(),
			},
			CmdArgs: NewCmdArgs(),
			Valid:   true,
			Enable:  true,
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
	*downloader
	regSpeed    *regexp.Regexp
	regProgress *regexp.Regexp
}

func (y *Youtubedl) FetchMediaInfo(task *Task) (*MediaInfo, error) {
	y.Reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagSimulateJson)
	jsonData, err := y.Exec()
	if err != nil {
		return nil, err
	}
	var mediaInfo YoutubedlMediaInfo
	err = json.Unmarshal(jsonData, &mediaInfo)
	if err != nil {
		return nil, err
	}
	return mediaInfo.toMediaInfo(), nil
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
	Title     string                  `json:"title,omitempty"`
	FullTitle string                  `json:"fullTitle,omitempty"`
	Desc      string                  `json:"description,omitempty"`
	Formats   []*youtubedlMediaFormat `json:"formats,omitempty"`
}

func (yMediaInfo *YoutubedlMediaInfo) toMediaInfo() *MediaInfo {
	yFormats := yMediaInfo.Formats
	formats := make([]*MediaFormat, 0, len(yFormats))
	for _, yFormat := range yMediaInfo.Formats {
		formats = append(formats, &MediaFormat{
			Format:   yFormat.Format,
			Id:       yFormat.FormatId,
			Url:      yFormat.Url,
			Ext:      yFormat.Ext,
			FileSize: yFormat.FileSize,
		})
	}
	return &MediaInfo{
		Title:     yMediaInfo.Title,
		FullTitle: yMediaInfo.FullTitle,
		Desc:      yMediaInfo.Desc,
		Formats:   formats,
	}
}

type youtubedlMediaFormat struct {
	Format   string `json:"format,omitempty"`
	FormatId string `json:"format_id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
