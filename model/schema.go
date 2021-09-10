/*
 @Author: ingbyr
*/

package model

import (
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/logging"
)

func SetupSchema() {
	err := db.DB.AutoMigrate(DownloaderTask{})
	if err != nil {
		logging.Panic("can not create database, %v", err)
	}
}
