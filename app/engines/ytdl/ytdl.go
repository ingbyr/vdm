/*
 @Author: ingbyr
*/

package ytdl

import (
	"encoding/json"
	"github.com/ingbyr/vdm/app/engine"
	"github.com/ingbyr/vdm/app/exec"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/ws"
	"path"
	"regexp"
	"strings"
)

const (
	name        = "youtube-dl"
	argNoColor  = "--no-color"
	argDumpJson = "--dump-json"
	argNewLine  = "--newline"
	argOutput   = "--output"
	argFormat   = "--format"
)

var (
	log   = logging.New("ytdl")
	_ytdl = &ytdl{
		Base: engine.Base{
			Version:      "local",
			Name:         name,
			ExecutorPath: executorPath,
			Valid:        true,
		},
		mediaNameTemplate: "%(title)s.%(ext)s",
		regSpeed:          regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress:       regexp.MustCompile("\\d+\\.?\\d*%"),
	}
)

func init() {
	log.Infow("register engine", "engine", name)
	engine.Register(_ytdl)
}

// ytdl downloader config core 'youtube-dl'
type ytdl struct {
	engine.Base
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
}

func (y *ytdl) FetchMediaInfo(task *task.MTask) (*media.Media, error) {
	execArgs := exec.NewArgs()
	execArgs.Add(task.MediaUrl)
	execArgs.Add(argDumpJson)
	mediaInfo := new(MediaInfo)
	output, err := exec.Cmd(y.ExecutorPath, execArgs.Args()...)
	if err != nil {
		return nil, err
	}
	if err = json.Unmarshal(output, mediaInfo); err != nil {
		return nil, err
	}
	return mediaInfo.standardize(), nil
}

func (y *ytdl) DownloadMedia(dTask *task.DTask) {
	execArgs := exec.NewArgs()
	execArgs.Add(dTask.MediaUrl)
	execArgs.Add(argNewLine)
	execArgs.Add(argNoColor)
	execArgs.AddV(argOutput, y.getStoragePath(dTask.StoragePath))
	if dTask.FormatId != "" {
		execArgs.AddV(argFormat, dTask.FormatId)
	}
	ws.AppendHeartbeatData(engine.HeartbeatDataTaskProgressGroup, dTask.ID.String(), dTask.Progress)
	taskCtx := exec.Context{
		Context:   engine.Ctx,
		Cancel:    engine.Cancel,
		OnNewLine: y.taskUpdateHandler(dTask),
		OnError:   y.taskErrorHandler(dTask),
		OnExit:    y.taskExitHandler(dTask),
	}
	exec.CmdAsnyc(taskCtx, y.ExecutorPath, execArgs.Args()...)
}

func (y *ytdl) taskUpdateHandler(dTask *task.DTask) func(line string) {
	return func(line string) {
		// update progress
		progressStr := y.regProgress.FindString(line)
		if progressStr != "" {
			dTask.Percent = progressStr[:len(progressStr)-1]
		}
		// update status
		dTask.Status = task.Downloading
		if strings.HasPrefix(dTask.Percent, engine.ProgressCompleted) || strings.Contains(line, "has already been downloaded") {
			dTask.Status = task.Completed
		}
		// update speed
		dTask.Speed = y.regSpeed.FindString(line)
		log.Debugw("update download task", "task", dTask)
	}
}

func (y *ytdl) taskErrorHandler(dTask *task.DTask) func(errMsg string) {
	return func(errMsg string) {
		dTask.Status = task.Failed
		dTask.StatusMsg = errMsg
	}
}

func (y *ytdl) taskExitHandler(dTask *task.DTask) func() {
	return func() {
		if dTask.Status == task.Downloading {
			dTask.Status = task.Paused
		}
		ws.InvokeHeartbeat()
		ws.RemoveHeartbeatData(engine.HeartbeatDataTaskProgressGroup, dTask.ID)
	}
}

func (y *ytdl) getStoragePath(storagePath string) string {
	return path.Join(storagePath, y.mediaNameTemplate)
}
