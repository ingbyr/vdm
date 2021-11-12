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
	if _, err := exec.LookPath(engine.GetBase().ExecutorPath); err != nil {
		engine.GetBase().Valid = false
		log.Warnf("engine '%s' is not valid because '%s' not found", engine.GetBase().Name, engine.GetBase().ExecutorPath)
	}
	m.Engines[engine.GetBase().Name] = engine
}

func Enabled(engine Engine) bool {
	_, ok := m.Engines[engine.GetBase().Name]
	return ok
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
