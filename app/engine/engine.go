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
	// HeartbeatDataTaskProgressGroup TODO rename this
	HeartbeatDataTaskProgressGroup = "taskProgress"
	ProgressCompleted              = "100"
)

var (
	log = logging.New("Base")
	ctx context.Context
)

func Setup(globalCtx context.Context) {
	ctx = globalCtx
}

// Engine is a common download engine
type Engine interface {
	GetBase() *Base

	// FetchMediaInfo TODO context
	FetchMediaInfo(task *task.MTask) (*media.Media, error)

	// DownloadMedia TODO context
	DownloadMedia(task *task.DTask)
}
