/*
 @Author: ingbyr
*/

package model

import (
	"encoding/json"
	"github.com/ingbyr/vdm/model/platform"
	"github.com/ingbyr/vdm/pkg/ws"
	"os"
	"regexp"
	"strings"
)

const (
	FlagNoColor       = "--no-color"
	FlagDumpJson      = "--dump-json"
	FlagNewLineOutput = "--newline"
	FlagOutput        = "--output"
	FlagFormat        = "--format"
)

var (
	decYdl = &DecYdl{
		decBase: &decBase{
			DecInfo: &DecInfo{
				Version:      "local",
				Name:         "youtube-dl",
				ExecutorPath: platform.DownloaderYoutubedlExecutorPath,
			},
			CmdArgs: NewCmdArgs(),
			Valid:   true,
		},
		mediaNameTemplate: "%(title)s.%(ext)s",
		regSpeed:          regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress:       regexp.MustCompile("\\d+\\.?\\d*%"),
	}
)

func init() {
	DecManager.Register(decYdl)
}

// DecYdl downloader engine core 'youtube-dl'
type DecYdl struct {
	*decBase
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
}

func (ydl *DecYdl) FetchMediaInfo(task *DTask) (*MediaInfo, error) {
	ydl.reset()
	ydl.CmdArgs.addCmdFlag(task.MediaUrl)
	ydl.CmdArgs.addCmdFlag(FlagDumpJson)
	output, err := ydl.ExecCmd()
	if err != nil {
		return nil, err
	}
	var yMediaInfo YdlMediaInfo
	err = json.Unmarshal(output, &yMediaInfo)
	if err != nil {
		return nil, err
	}
	mediaInfo := yMediaInfo.toMediaInfo()
	task.MediaBaseInfo = mediaInfo.MediaBaseInfo
	return mediaInfo, nil
}

func (ydl *DecYdl) Download(task *DTask) {
	ydl.reset()
	ydl.addCmdFlag(task.MediaUrl)
	ydl.addCmdFlag(FlagNewLineOutput)
	ydl.addCmdFlag(FlagNoColor)
	ydl.addCmdFlagValue(FlagOutput, ydl.getStoragePath(task.StoragePath))
	if task.FormatId != "" {
		ydl.addCmdFlagValue(FlagFormat, task.FormatId)
	}
	ws.AppendHeartbeatData(HeartbeatDataTaskProgressGroup, task.ID.String(), task.DTaskProgress)
	ydl.ExecCmdLong(task,
		ydl.downloaderTaskUpdateHandler,
		ydl.downloadTaskFinalHandler,
		ydl.downloadTaskErrorHandler)
}

func (ydl *DecYdl) downloaderTaskUpdateHandler(_task interface{}, line string) {
	task := _task.(*DTask)
	// update progress
	progressStr := ydl.regProgress.FindString(line)
	if progressStr != "" {
		task.Progress = progressStr[:len(progressStr)-1]
	}

	// update status
	task.Status = TaskStatusRunning
	if strings.HasPrefix(task.Progress, ProgressCompleted) || strings.Contains(line, "has already been downloaded") {
		task.Status = TaskStatusCompleted
	}

	// update speed
	task.Speed = ydl.regSpeed.FindString(line)
}

func (ydl *DecYdl) downloadTaskFinalHandler(_task interface{}) {
	task := _task.(*DTask)
	if task.Status == TaskStatusRunning {
		task.Status = TaskStatusPaused
	}
	ws.InvokeHeartbeat()
	ws.RemoveHeartbeatData(HeartbeatDataTaskProgressGroup, task.ID)
}

func (ydl *DecYdl) downloadTaskErrorHandler(_task interface{}, err string) {
	task := _task.(*DTask)
	task.Status = TaskStatusError
}

func (ydl *DecYdl) getStoragePath(storagePath string) string {
	pathSeparator := string(os.PathSeparator)
	if strings.HasSuffix(storagePath, pathSeparator) {
		return storagePath + ydl.mediaNameTemplate
	}
	return storagePath + pathSeparator + ydl.mediaNameTemplate
}

func (ydl *DecYdl) reset() {
	ydl.CmdArgs = NewCmdArgs()
}

type YdlMediaInfo struct {
	Title     string                  `json:"title,omitempty"`
	FullTitle string                  `json:"fullTitle,omitempty"`
	Desc      string            `json:"description,omitempty"`
	Formats   []*ydlMediaFormat `json:"formats,omitempty"`
}

func (yi *YdlMediaInfo) toMediaInfo() *MediaInfo {
	yFormats := yi.Formats
	formats := make([]*MediaFormat, 0, len(yFormats))
	for _, yFormat := range yi.Formats {
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
			Title: yi.Title,
			Desc:  yi.Desc,
		},
		Formats: formats,
	}
}

type ydlMediaFormat struct {
	Format   string `json:"format,omitempty"`
	FormatId string `json:"format_id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
