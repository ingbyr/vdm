//go:build linux || darwin
// +build linux darwin

package platform

import (
	"github.com/ingbyr/vdm/pkg/setting"
	"path/filepath"
)

var (
	EngineYtdlExecutorPath = filepath.Join(setting.DirRuntime, setting.DirEngine, "yt-dlp_macos")
)
