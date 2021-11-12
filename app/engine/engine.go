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
	FetchMediaInfo(mtask *task.MTask) (*media.Media, error)

	// DownloadMedia TODO context
	DownloadMedia(dtask *task.DTask) error

	// Broadcast the current download task data
	Broadcast(dtask *task.DTask)
}
