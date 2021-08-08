/*
 @Author: ingbyr
*/

package db

import (
	"fmt"
	"github.com/ingbyr/vdm/model/downloader"
	"github.com/ingbyr/vdm/pkg/logging"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var Db *gorm.DB

func Setup(dbPath string) {
	logging.Debug("connecting db '%s' ...", dbPath)
	db, err := gorm.Open(sqlite.Open(dbPath), &gorm.Config{})
	if err != nil {
		logging.Panic("failed to connect database")
	}
	Db = db
	logging.Debug("connected")

	logging.Debug("migrating the schema ...")
	Db.AutoMigrate(&downloader.Task{})
	Db.AutoMigrate(&downloader.TaskConfig{})
}

func InitSchemes() {
	schemes := []string{
		`create table if not exists task
		(
			id         integer primary key,
			created_at text,
			status     integer,
			title      text,
			desc       text,
			config     integer,
			download_size text,
			progress      real,
			speed         real
		)`,
		`create table if not exists task_config
		(
			task_id      integer primary key,
			media_url    text,
			downloader   text,
			storage_path text,
			format_id    text,
			format_url   text
		)`,
	}
	fmt.Println(schemes)
}
