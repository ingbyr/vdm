/*
 @Author: ingbyr
*/

package ytdl

import (
	"github.com/ingbyr/vdm/app/engine"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/ws"
	"os"
	"regexp"
	"strings"
)

const (
	EngineName  = "youtube-dl"
	cmdNoColor  = "--no-color"
	cmdDumpJson = "--dump-json"
	cmdNewLine  = "--newline"
	cmdOutput   = "--output"
	cmdFormat   = "--format"
)

var (
	log   = logging.New("ytdl")
	_ytdl = &ytdl{
		Base: engine.Base{
			Version:      "local",
			Name:         EngineName,
			ExecutorPath: executorPath,
			Valid:        true,
		},
		mediaNameTemplate: "%(title)s.%(ext)s",
		regSpeed:          regexp.MustCompile("\\d+\\.?\\d*\\w+/s"),
		regProgress:       regexp.MustCompile("\\d+\\.?\\d*%"),
	}
)

func init() {
	log.Infow("register engine", "engine", EngineName)
	engine.Register(_ytdl)
}

// ytdl downloader config core 'youtube-dl'
type ytdl struct {
	engine.Base
	mediaNameTemplate string
	regSpeed          *regexp.Regexp
	regProgress       *regexp.Regexp
}

func (y *ytdl) Copy() *ytdl {
	return nil
}

func (y *ytdl) FetchMediaInfo(task *task.MTask) (*media.Media, error) {
	cmdArgs := y.NewCmdArgs()
	cmdArgs.Add(task.MediaUrl)
	cmdArgs.Add(cmdDumpJson)
	mediaInfo := new(MediaInfo)
	if err := y.ExecCmd(mediaInfo, cmdArgs); err != nil {
		return nil, err
	}
	return mediaInfo.standardize(), nil
}

func (y *ytdl) DownloadMedia(task *task.DTask) {
	cmdArgs := y.NewCmdArgs()
	cmdArgs.Add(task.MediaUrl)
	cmdArgs.Add(cmdNewLine)
	cmdArgs.Add(cmdNoColor)
	cmdArgs.AddV(cmdOutput, y.getStoragePath(task.StoragePath))
	if task.FormatId != "" {
		cmdArgs.AddV(cmdFormat, task.FormatId)
	}
	ws.AppendHeartbeatData(engine.HeartbeatDataTaskProgressGroup, task.ID.String(), task.Progress)
	// TODO
	//y.ExecCmdLong(task,
	//	y.downloaderTaskUpdateHandler,
	//	y.downloadTaskFinalHandler,
	//	y.downloadTaskErrorHandler)
}

func (y *ytdl) downloaderTaskUpdateHandler(_task interface{}, line string) {
	t := _task.(*task.DTask)
	// update progress
	progressStr := y.regProgress.FindString(line)
	if progressStr != "" {
		t.Percent = progressStr[:len(progressStr)-1]
	}

	// update status
	t.Status = task.Downloading
	if strings.HasPrefix(t.Percent, engine.ProgressCompleted) || strings.Contains(line, "has already been downloaded") {
		t.Status = task.Completed
	}

	// update speed
	t.Speed = y.regSpeed.FindString(line)
}

func (y *ytdl) downloadTaskFinalHandler(_task interface{}) {
	t := _task.(*task.DTask)
	if t.Status == task.Downloading {
		t.Status = task.Paused
	}
	ws.InvokeHeartbeat()
	ws.RemoveHeartbeatData(engine.HeartbeatDataTaskProgressGroup, t.ID)
}

func (y *ytdl) downloadTaskErrorHandler(_task interface{}, err string) {
	t := _task.(*task.DTask)
	t.Status = task.Failed
}

func (y *ytdl) getStoragePath(storagePath string) string {
	pathSeparator := string(os.PathSeparator)
	if strings.HasSuffix(storagePath, pathSeparator) {
		return storagePath + y.mediaNameTemplate
	}
	return storagePath + pathSeparator + y.mediaNameTemplate
}
