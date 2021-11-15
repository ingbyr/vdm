/*
 @Author: ingbyr
*/

package schema

import (
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/store"
)

var log = logging.New("db")

func Setup() {
	err := store.DB.AutoMigrate(task.DTask{})
	if err != nil {
		log.Panic("can not create database, %v", err)
	}
}
