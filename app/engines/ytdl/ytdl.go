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
			Version:  "local",
			Name:     name,
			Executor: executorPath,
			Enable:   true,
			Valid:    true,
		},
		mediaNameTemplate: "%(title)s.%(ext)s",
		regSpeed:          regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress:       regexp.MustCompile("\\d+\\.?\\d*%"),
		progressCompleted: "100",
	}
)

func init() {
	log.Infow("register engine", "engine", name)
	engine.Register(_ytdl)
}

// ytdl from https://github.com/yt-dlp/yt-dlp
type ytdl struct {
	engine.Base
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
	progressCompleted string
}

func (y *ytdl) FetchMediaFormats(mtask *task.MTask) (*media.Formats, error) {
	execArgs := exec.NewArgs(y.Executor)
	execArgs.Add(mtask.MediaUrl)
	execArgs.Add(argDumpJson)
	mediaInfo := new(MediaInfo)
	output, err := exec.Cmd(mtask.Ctx, execArgs)
	if err != nil {
		return nil, err
	}
	if err = json.Unmarshal(output, mediaInfo); err != nil {
		log.Errorw("unmarshal error", "err", err)
		return nil, err
	}
	return mediaInfo.standardize(mtask), nil
}

func (y *ytdl) DownloadMedia(dtask *task.DTask) error {
	execArgs := exec.NewArgs(y.Executor)
	execArgs.Add(dtask.Url)
	execArgs.Add(argNewLine)
	execArgs.Add(argNoColor)
	execArgs.AddV(argOutput, y.getStoragePath(dtask.StoragePath))
	if dtask.FormatId != "" {
		execArgs.AddV(argFormat, dtask.FormatId)
	}
	callback := exec.Callback{
		OnNewLine: y.taskUpdateHandler(dtask),
		OnError:   y.taskErrorHandler(dtask),
		OnExit:    y.taskExitHandler(dtask),
	}
	exec.CmdAsnyc(dtask.Ctx, dtask.Cancel, callback, execArgs)
	return nil
}

func (y *ytdl) taskUpdateHandler(dtask *task.DTask) func(line string) {
	return func(line string) {
		// update progress
		progressStr := y.regProgress.FindString(line)
		if progressStr != "" {
			dtask.Progress.Percent = progressStr[:len(progressStr)-1]
		}
		// update status
		dtask.Progress.Status = task.Downloading
		if strings.HasPrefix(dtask.Progress.Percent, y.progressCompleted) || strings.Contains(line, "has already been downloaded") {
			dtask.Progress.Status = task.Completed
		}
		// update speed
		dtask.Progress.Speed = y.regSpeed.FindString(line)
		log.Debugw("update download task", "task", dtask)
		y.BroadcastProgress(dtask.Progress)
	}
}

func (y *ytdl) taskErrorHandler(dtask *task.DTask) func(errMsg string) {
	return func(errMsg string) {
		dtask.Progress.Status = task.Failed
		dtask.Progress.CmdOutput = errMsg
		y.BroadcastProgress(dtask.Progress)
	}
}

func (y *ytdl) taskExitHandler(dtask *task.DTask) func() {
	return func() {
		if dtask.Progress.Status == task.Downloading {
			dtask.Progress.Status = task.Paused
		}
		y.BroadcastProgress(dtask.Progress)
	}
}

func (y *ytdl) getStoragePath(storagePath string) string {
	return path.Join(storagePath, y.mediaNameTemplate)
}
