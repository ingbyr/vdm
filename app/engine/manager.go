/*
 @Author: ingbyr
*/

package engine

import (
	"context"
	"fmt"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/db"
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
	if !engine.GetInfo().Enable {
		return
	}
	if _, err := exec.LookPath(engine.GetInfo().Executor); err != nil {
		engine.GetInfo().Valid = false
		log.Warnf("engine '%s' is not valid because '%s' not found", engine.GetInfo().Name, engine.GetInfo().Executor)
	}
	m.Engines[engine.GetInfo().Name] = engine
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
	dtask.Model = db.NewModel()
	dtask.Ctx, dtask.Cancel = context.WithCancel(ctx)
	return engine.DownloadMedia(dtask)
}
