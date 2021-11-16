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

func GetMediaFormats(mtask *task.MTask) (*media.Formats, error) {
	engine, ok := m.Engines[mtask.Engine]
	if !ok {
		return nil, fmt.Errorf("can not found engine %s", mtask.Engine)
	}
	mtask.Ctx, mtask.Cancel = context.WithTimeout(ctx, 10*time.Second)
	return engine.GetMediaFormats(mtask)
}

func DownloadMedia(dtask *task.DTask) ([]task.DTask, error) {
	engine, ok := m.Engines[dtask.Engine]
	if !ok {
		return nil, fmt.Errorf("can not found engine %s", dtask.Engine)
	}
	dtask.Ctx, dtask.Cancel = context.WithCancel(ctx)
	dtask.Save()
	if err := engine.DownloadMedia(dtask); err != nil {
		return nil, err
	}
	return []task.DTask{*dtask}, nil
}
