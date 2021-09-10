// +build linux darwin

package platform

import (
	"github.com/ingbyr/vdm/pkg/setting"
	"path/filepath"
)

var (
	DownloaderYoutubedlExecutorPath = filepath.Join(setting.DirRuntime, "engine","youtube-dl")
)

