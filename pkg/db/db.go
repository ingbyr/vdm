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
	logging.Debug("connecting db '%s' ...", dbPath)
	DB, err = gorm.Open(sqlite.Open(dbPath), &gorm.Config{
		Logger: logging.DBLogger,
	})
	if err != nil {
		logging.Panic("failed to connect database")
	}
	logging.Debug("connected")

	logging.Debug("migrating the schema ...")
}
