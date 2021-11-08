/*
 @Author: ingbyr
*/

package model

import (
	"github.com/ingbyr/vdm/pkg/db"
)

func SetupSchema() {
	err := db.DB.AutoMigrate(DTask{})
	if err != nil {
		log.Panic("can not create database, %v", err)
	}
}
