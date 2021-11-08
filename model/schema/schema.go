/*
 @Author: ingbyr
*/

package db

import (
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/logging"
)

var log = logging.New("db")

func SetupSchema() {
	err := db.DB.AutoMigrate(task.Task{})
	if err != nil {
		log.Panic("can not create database, %v", err)
	}
}
