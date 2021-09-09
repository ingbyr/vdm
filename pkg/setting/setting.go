/*
 @Author: ingbyr
*/

package setting

import (
	"github.com/go-ini/ini"
	"github.com/ingbyr/vdm/pkg/logging"
	"time"
)

type Server struct {
	RunMode      string
	HttpPort     int
	ReadTimeout  time.Duration
	WriteTimeout time.Duration
}

type App struct {
	HeartbeatInterval time.Duration
	DatabasePath      string
}

type Log struct {
	Level string
}

var (
	ServerSetting = &Server{}
	AppSetting    = &App{}
	LogSetting    = &Log{}
)

const (
	DirRuntime = "runtime"
)

var cfg *ini.File

func Setup() {
	var err error
	cfg, err = ini.Load("conf/app.ini")
	if err != nil {
		logging.Panic("fail to parse 'conf/app.ini': %v", err)
	}

	loadSection("app", AppSetting)
	loadSection("server", ServerSetting)
	loadSection("log", &LogSetting)

	ServerSetting.ReadTimeout = ServerSetting.ReadTimeout * time.Second
	ServerSetting.WriteTimeout = ServerSetting.WriteTimeout * time.Second
}

func loadSection(section string, v interface{}) {
	err := cfg.Section(section).MapTo(v)
	if err != nil {
		logging.Panic("load config section '%s' failed: %v", section, err)
	}
}
