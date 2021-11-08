/*
 @Author: ingbyr
*/

package db

import (
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/setting"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var DB *gorm.DB

var log = logging.New("db")

func Setup() {
	if DB != nil {
		return
	}
	var err error
	var dbPath string
	if setting.AppSetting == nil || setting.AppSetting.DatabasePath == "" {
		dbPath = "tmp.db"
	} else {
		dbPath = setting.AppSetting.DatabasePath
	}
	log.Debug("connecting db '%s' ...", dbPath)
	DB, err = gorm.Open(sqlite.Open(dbPath), &gorm.Config{
		Logger: logging.Gorm(),
	})
	if err != nil {
		log.Panic("failed to connect database")
	}
	log.Debug("connected")

	log.Debug("migrating the schema ...")
}
