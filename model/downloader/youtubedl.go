/*
 @Author: ingbyr
*/

package downloader

import (
	"encoding/json"
	"github.com/ingbyr/vdm/model/goos"
	"os"
	"regexp"
	"runtime"
	"strings"
)

const (
	FlagDumpJson      = "--dump-json"
	FlagNewLineOutput = "--newline"
	FlagOutput        = "--output"
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
		mediaNameTemplate: "%(title)s.%(ext)s",
		regSpeed:          regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress:       regexp.MustCompile("\\d+\\.?\\d*%"),
	}
)

func init() {
	_ = Manager.Register(youtubedl)
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
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
}

func (y *Youtubedl) FetchMediaInfo(task *Task) (*MediaInfo, error) {
	y.Reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagDumpJson)
	yMediaInfoData, err := y.Exec()
	if err != nil {
		return nil, err
	}
	var yMediaInfo YoutubedlMediaInfo
	err = json.Unmarshal(yMediaInfoData, &yMediaInfo)
	if err != nil {
		return nil, err
	}
	mediaInfo := yMediaInfo.toMediaInfo()
	task.MediaBaseInfo = mediaInfo.MediaBaseInfo
	return mediaInfo, nil
}

func (y *Youtubedl) Download(task *Task) {
	y.Reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagNewLineOutput)
	y.CmdArgs.addFlagValue(FlagOutput, y.GenerateStoragePath(task.StoragePath))
	y.ExecAsync(task, y.UpdateTask)
}

func (y *Youtubedl) GenerateStoragePath(storagePath string) string {
	pathSeparator := string(os.PathSeparator)
	if strings.HasSuffix(storagePath, pathSeparator) {
		return storagePath + y.mediaNameTemplate
	}
	return storagePath + pathSeparator + y.mediaNameTemplate
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
		MediaBaseInfo: &MediaBaseInfo{
			Title: yMediaInfo.Title,
			Desc:  yMediaInfo.Desc,
		},
		Formats: formats,
	}
}

type youtubedlMediaFormat struct {
	Format   string `json:"format,omitempty"`
	FormatId string `json:"format_id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
