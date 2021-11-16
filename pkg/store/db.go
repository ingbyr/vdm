/*
 @Author: ingbyr
*/

package store

import (
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/setting"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var DB *gorm.DB

var log = logging.New("store")

func Setup() {
	if DB != nil {
		panic("can not setup database again")
	}
	var err error
	var dbPath string
	if setting.AppSetting == nil || setting.AppSetting.DatabasePath == "" {
		dbPath = "tmp.db"
	} else {
		dbPath = setting.AppSetting.DatabasePath
	}
	log.Debugw("load db", "path", dbPath)
	DB, err = gorm.Open(sqlite.Open(dbPath), &gorm.Config{
		Logger: logging.Gorm(),
	})
	if err != nil {
		log.Panic("failed to connect database")
	}
}
