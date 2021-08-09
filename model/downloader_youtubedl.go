/*
 @Author: ingbyr
*/

package model

import (
	"encoding/json"
	"github.com/ingbyr/vdm/pkg/pt"
	"github.com/ingbyr/vdm/pkg/ws"
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
			DownloaderInfo: &DownloaderInfo{
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
	DownloaderManager.Register(youtubedl)
}

func GetYoutubedlExecutorPath() string {
	switch runtime.GOOS {
	case pt.Windows:
		return ".\\runtime\\engine\\youtube-dl.exe"
	case pt.Linux:
		return "./runtime/engine/youtube-dl"
	case pt.MacOS:
		return "./runtime/engine/youtube-dl"
	default:
		return "not support platform " + runtime.GOOS
	}
}

type Youtubedl struct {
	*downloader
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
}

func (y *Youtubedl) FetchMediaInfo(task *DownloaderTask) (*MediaInfo, error) {
	y.reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagDumpJson)
	yMediaInfoData, err := y.execCmd()
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

func (y *Youtubedl) Download(task *DownloaderTask) {
	y.reset()
	y.CmdArgs.addFlag(task.MediaUrl)
	y.CmdArgs.addFlag(FlagNewLineOutput)
	y.CmdArgs.addFlagValue(FlagOutput, y.getStoragePath(task.StoragePath))
	ws.AppendHeartbeatData(HeartbeatDataTaskProgressGroup, task.ID, task.DownloaderTaskProgress)
	y.execCmdAsync(task, y.UpdateDownloaderTask, y.FinishDownloadTask)
}

func (y *Youtubedl) UpdateDownloaderTask(_task interface{}, line string) {
	task := _task.(*DownloaderTask)
	// update progress
	progressStr := y.regProgress.FindString(line)
	if progressStr != "" {
		task.Progress = progressStr[:len(progressStr)-1]
	}

	// update status
	task.Status = TaskStatusRunning
	if strings.HasPrefix(task.Progress, ProgressCompleted) || strings.Contains(line, "has already been downloaded") {
		task.Status = TaskStatusCompleted
	}

	// update speed
	task.Speed = y.regSpeed.FindString(line)
}

func (y *Youtubedl) FinishDownloadTask(_task interface{}) {
	task := _task.(*DownloaderTask)
	if task.Status != TaskStatusCompleted {
		task.Status = TaskStatusPaused
	}
	ws.InvokeHeartbeat()
	ws.RemoveHeartbeatData(HeartbeatDataTaskProgressGroup, task.ID)
}

func (y *Youtubedl) getStoragePath(storagePath string) string {
	pathSeparator := string(os.PathSeparator)
	if strings.HasSuffix(storagePath, pathSeparator) {
		return storagePath + y.mediaNameTemplate
	}
	return storagePath + pathSeparator + y.mediaNameTemplate
}

func (y *Youtubedl) reset() {
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
