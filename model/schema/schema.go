/*
 @Author: ingbyr
*/

package schema

import (
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/logging"
)

var log = logging.New("db")

func Setup() {
	err := db.DB.AutoMigrate(task.DTask{})
	if err != nil {
		log.Panic("can not create database, %v", err)
	}
}
