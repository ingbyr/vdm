// +build windows

package platform

import (
	"github.com/ingbyr/vdm/pkg/setting"
	"path/filepath"
)

var (
	EngineYtdlExecutorPath = filepath.Join(setting.DirRuntime, "engine", "youtube-dl.exe")
)