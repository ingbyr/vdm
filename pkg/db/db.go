/*
 @Author: ingbyr
*/

package db

import (
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/setting"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var Db *gorm.DB

func Setup() {
	var err error
	var dbPath string
	if setting.AppSetting == nil || setting.AppSetting.DatabasePath == "" {
		dbPath = "tmp.db"
	} else {
		dbPath = setting.AppSetting.DatabasePath
	}
	logging.Debug("connecting db '%s' ...", dbPath)
	Db, err = gorm.Open(sqlite.Open(dbPath), &gorm.Config{})
	if err != nil {
		logging.Panic("failed to connect database")
	}
	logging.Debug("connected")

	logging.Debug("migrating the schema ...")
	Db.AutoMigrate(&model.DownloaderTask{})
	Db.AutoMigrate(&model.DownloaderTaskConfig{})
}
