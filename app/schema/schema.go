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
	migrate(task.DTask{})
	migrate(task.Progress{})
}

func migrate(table interface{}) {
	err := store.DB.AutoMigrate(table)
	if err != nil {
		log.Panicw("failed migrate", "err", err)
	}
}
