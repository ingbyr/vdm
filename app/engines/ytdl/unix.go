//go:build linux || darwin
// +build linux darwin

package ytdl

import (
	"github.com/ingbyr/vdm/pkg/setting"
	"path/filepath"
)

var (
	executorPath = filepath.Join(setting.DirRuntime, setting.DirEngine, "yt-dlp_macos")
)
