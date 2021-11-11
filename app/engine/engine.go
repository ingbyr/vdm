/*
 @Author: ingbyr
*/

package engine

import (
	"context"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/logging"
)

const (
	HeartbeatDataTaskProgressGroup = "taskProgress"
	ProgressCompleted              = "100"
)

var (
	log    = logging.New("Base")
	ctx    context.Context
	cancel context.CancelFunc
)

func Setup(globalCtx context.Context, globalCancel context.CancelFunc) {
	ctx = globalCtx
	cancel = globalCancel
}

// Engine is media downloader
type Engine interface {
	GetBase() *Base
	FetchMediaInfo(task *task.MTask) (*media.Media, error)
	DownloadMedia(task *task.DTask)
}
