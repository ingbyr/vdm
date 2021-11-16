/*
 @Author: ingbyr
*/

package engine

import (
	"context"
	"fmt"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
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
	if !engine.GetBase().Enable {
		return
	}
	if _, err := exec.LookPath(engine.GetBase().Executor); err != nil {
		engine.GetBase().Valid = false
		log.Warnw("engine is not valid",
			"engine", engine.GetBase().Name,
			"notFound", engine.GetBase().Executor)
	}
	m.Engines[engine.GetBase().Name] = engine
}

func FetchMediaInfo(mtask *task.MTask) (*media.Formats, error) {
	engine, ok := m.Engines[mtask.Engine]
	if !ok {
		return nil, fmt.Errorf("can not found engine %s", mtask.Engine)
	}
	mtask.Ctx, mtask.Cancel = context.WithTimeout(ctx, 10*time.Second)
	return engine.FetchMediaFormats(mtask)
}

func DownloadMedia(dtask *task.DTask) error {
	engine, ok := m.Engines[dtask.Engine]
	if !ok {
		return fmt.Errorf("can not found engine %s", dtask.Engine)
	}
	// TODO check same task in db
	dtask.Ctx, dtask.Cancel = context.WithCancel(ctx)
	return engine.DownloadMedia(dtask)
}
