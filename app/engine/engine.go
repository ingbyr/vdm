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
	// GetBase get engine base info
	GetBase() *Base

	// GetMediaFormats fetch media json format info
	GetMediaFormats(mtask *task.MTask) (*media.Formats, error)

	// DownloadMedia download specified media
	DownloadMedia(dtask *task.DTask) error

	// Broadcast the current download task status
	Broadcast(dtask *task.DTask)
}
