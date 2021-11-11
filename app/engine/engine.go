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
	Ctx    context.Context
	Cancel context.CancelFunc
)

func Setup(globalCtx context.Context, globalCancel context.CancelFunc) {
	Ctx = globalCtx
	Cancel = globalCancel
}

// Engine is media downloader
type Engine interface {
	GetBase() *Base

	// FetchMediaInfo TODO context
	FetchMediaInfo(task *task.MTask) (*media.Media, error)

	// DownloadMedia TODO context
	DownloadMedia(task *task.DTask)
}
