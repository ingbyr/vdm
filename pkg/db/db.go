/*
 @Author: ingbyr
*/

package db

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/setting"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"strconv"
)

var DB *gorm.DB

func Setup() {
	var err error
	var dbPath string
	if setting.AppSetting == nil || setting.AppSetting.DatabasePath == "" {
		dbPath = "tmp.db"
	} else {
		dbPath = setting.AppSetting.DatabasePath
	}
	logging.Debug("connecting db '%s' ...", dbPath)
	DB, err = gorm.Open(sqlite.Open(dbPath), &gorm.Config{})
	if err != nil {
		logging.Panic("failed to connect database")
	}
	logging.Debug("connected")

	logging.Debug("migrating the schema ...")
	DB.AutoMigrate(&model.DownloaderTask{})
}

func Page(c *gin.Context) func(db *gorm.DB) *gorm.DB {
	return func(db *gorm.DB) *gorm.DB {
		page, _ := strconv.Atoi(c.DefaultQuery("page", "1"))
		pageSize, _ := strconv.Atoi(c.DefaultQuery("pageSize", "100"))
		if pageSize > 100 {
			pageSize = 100
		}
		offset := (page - 1) * pageSize
		return db.Offset(offset).Limit(pageSize)
	}
}
