// +build windows

package ytdl

import (
	"github.com/ingbyr/vdm/pkg/setting"
	"path/filepath"
)

var (
	executorPath = filepath.Join(setting.DirRuntime, "engine", "youtube-dl.exe")
)