// +build windows

package platform

import (
	"github.com/ingbyr/vdm/pkg/setting"
	"path/filepath"
)

var (
	DownloaderYoutubedlExecutorPath = filepath.Join(setting.DirRuntime, "youtube-dl.exe")
)