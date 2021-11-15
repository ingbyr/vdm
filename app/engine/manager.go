/*
 @Author: ingbyr
*/

package engine

import (
	"context"
	"fmt"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/store"
	"os/exec"
	"time"
)

var (
	m = &manager{
		Engines: make(map[string]Engine),
	}
)

type manager struct {
	Engines map[string]Engine `json:"engines,omitempty"`
}

func Engines() map[string]Engine {
	return m.Engines
}

func Register(engine Engine) {
	if !engine.GetConfig().Enable {
		return
	}
	if _, err := exec.LookPath(engine.GetConfig().Executor); err != nil {
		engine.GetConfig().Valid = false
		log.Warnf("engine '%s' is not valid because '%s' not found", engine.GetConfig().Name, engine.GetConfig().Executor)
	}
	m.Engines[engine.GetConfig().Name] = engine
}

func FetchMediaInfo(mtask *task.MTask) (*media.Media, error) {
	engine, ok := m.Engines[mtask.Engine]
	if !ok {
		return nil, fmt.Errorf("can not found engine %s", mtask.Engine)
	}
	mtask.Ctx, mtask.Cancel = context.WithTimeout(ctx, 10*time.Second)
	return engine.FetchMediaInfo(mtask)
}

func DownloadMedia(dtask *task.DTask) error {
	engine, ok := m.Engines[dtask.Engine]
	if !ok {
		return fmt.Errorf("can not found engine %s", dtask.Engine)
	}
	// TODO check same task in db
	dtask.Model = store.NewModel()
	dtask.Ctx, dtask.Cancel = context.WithCancel(ctx)
	return engine.DownloadMedia(dtask)
}
