/*
 @Author: ingbyr
*/

package engine

import (
	"encoding/json"
	"github.com/ingbyr/vdm/model/media"
	"github.com/ingbyr/vdm/model/platform"
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/ws"
	"os"
	"regexp"
	"strings"
)

const (
	OptNoColor       = "--no-color"
	OptDumpJson      = "--dump-json"
	OptNewLineOutput = "--newline"
	OptOutput        = "--output"
	OptFormat        = "--format"
)

var (
	_ytdl = &ytdl{
		engine: engine{
			Version:      "local",
			Name:         "youtube-dl",
			ExecutorPath: platform.EngineYtdlExecutorPath,
			Valid:        true,
		},
		mediaNameTemplate: "%(title)s.%(ext)s",
		regSpeed:          regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress:       regexp.MustCompile("\\d+\\.?\\d*%"),
	}
)

func init() {
	register(_ytdl)
}

// ytdl downloader engine core 'youtube-dl'
type ytdl struct {
	engine
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
}

func (y *ytdl) FetchMediaInfo(task *task.MTask) (*media.Info, error) {
	y.reset()
	y.addCmdFlag(task.MediaUrl)
	y.addCmdFlag(OptDumpJson)
	output, err := y.ExecCmd()
	if err != nil {
		return nil, err
	}
	ytdlMediaInfo := new(YtdlMediaInfo)
	err = json.Unmarshal(output, ytdlMediaInfo)
	if err != nil {
		return nil, err
	}
	// TODO use interface
	mediaInfo := ytdlMediaInfo.toMediaInfo()
	return mediaInfo, nil
}

func (y *ytdl) Download(task *task.DTask) {
	y.reset()
	y.addCmdFlag(task.MediaUrl)
	y.addCmdFlag(OptNewLineOutput)
	y.addCmdFlag(OptNoColor)
	y.addCmdFlagValue(OptOutput, y.getStoragePath(task.StoragePath))
	if task.FormatId != "" {
		y.addCmdFlagValue(OptFormat, task.FormatId)
	}
	ws.AppendHeartbeatData(HeartbeatDataTaskProgressGroup, task.ID.String(), task.Progress)
	y.ExecCmdLong(task,
		y.downloaderTaskUpdateHandler,
		y.downloadTaskFinalHandler,
		y.downloadTaskErrorHandler)
}

func (y *ytdl) downloaderTaskUpdateHandler(_task interface{}, line string) {
	t := _task.(*task.DTask)
	// update progress
	progressStr := y.regProgress.FindString(line)
	if progressStr != "" {
		t.Percent = progressStr[:len(progressStr)-1]
	}

	// update status
	t.Status = task.StatusRunning
	if strings.HasPrefix(t.Percent, ProgressCompleted) || strings.Contains(line, "has already been downloaded") {
		t.Status = task.StatusCompleted
	}

	// update speed
	t.Speed = y.regSpeed.FindString(line)
}

func (y *ytdl) downloadTaskFinalHandler(_task interface{}) {
	t := _task.(*task.DTask)
	if t.Status == task.StatusRunning {
		t.Status = task.StatusPaused
	}
	ws.InvokeHeartbeat()
	ws.RemoveHeartbeatData(HeartbeatDataTaskProgressGroup, t.ID)
}

func (y *ytdl) downloadTaskErrorHandler(_task interface{}, err string) {
	t := _task.(*task.DTask)
	t.Status = task.StatusError
}

func (y *ytdl) getStoragePath(storagePath string) string {
	pathSeparator := string(os.PathSeparator)
	if strings.HasSuffix(storagePath, pathSeparator) {
		return storagePath + y.mediaNameTemplate
	}
	return storagePath + pathSeparator + y.mediaNameTemplate
}

func (y *ytdl) reset() {
	y.opts = EmptyOpts()
}

type YtdlMediaInfo struct {
	Title     string             `json:"title,omitempty"`
	FullTitle string             `json:"fullTitle,omitempty"`
	Desc      string             `json:"description,omitempty"`
	Formats   []*ytdlMediaFormat `json:"formats,omitempty"`
}

type ytdlMediaFormat struct {
	Format   string `json:"format,omitempty"`
	FormatId string `json:"format_id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}

func (m *YtdlMediaInfo) toMediaInfo() *media.Info {
	yFormats := m.Formats
	formats := make([]*media.Format, 0, len(yFormats))
	for _, yFormat := range m.Formats {
		formats = append(formats, &media.Format{
			Format:   yFormat.Format,
			Id:       yFormat.FormatId,
			Url:      yFormat.Url,
			Ext:      yFormat.Ext,
			FileSize: yFormat.FileSize,
		})
	}
	return &media.Info{
		Base: media.Base{
			Title: m.Title,
			Desc:  m.Desc,
		},
		Formats: formats,
	}
}
