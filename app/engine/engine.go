/*
 @Author: ingbyr
*/

package engine

import (
	"context"
	"encoding/json"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/ws"
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
	GetName() string
	GetVersion() string
	GetExecutor() string
	isEnable() bool
	isValid() bool
	SetValid(valid bool)
	FetchMediaFormats(mtask *task.MTask) (*media.Formats, error)
	DownloadMedia(dtask *task.DTask) error
	Broadcast(dtask *task.DTask)
}

var _ Engine = (*Base)(nil)

type Base struct {
	// Name is a common name for engine
	Name string `json:"name"`

	// Version is engine executor binary version
	Version string `json:"version"`

	// Executor is engine executor binary path
	Executor string `json:"executor"`

	// Enable if enable is false, the engine will not register to manager
	Enable bool `json:"enable"`

	// Valid is false if engine path not existed or not an executable binary
	Valid bool `json:"valid"`
}

func (b *Base) GetName() string {
	return b.Name
}

func (b *Base) GetVersion() string {
	return b.Version
}

func (b *Base) GetExecutor() string {
	return b.Executor
}

func (b *Base) isEnable() bool {
	return b.Enable
}

func (b *Base) isValid() bool {
	return b.Valid
}

func (b *Base) SetValid(valid bool) {
	b.Valid = valid
}

func (b *Base) FetchMediaFormats(mTask *task.MTask) (*media.Formats, error) {
	panic("implement me")
}

func (b *Base) DownloadMedia(dTask *task.DTask) error {
	panic("implement me")
}

func (b *Base) Broadcast(dtask *task.DTask) {
	dtask.SaveProgress()
	progress, _ := json.Marshal(dtask.Progress)
	ws.Broadcast(progress)
}
