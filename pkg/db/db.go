/*
 @Author: ingbyr
*/

package db

import (
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/jmoiron/sqlx"
)

const (
	dbType = "sqlite3"
)

var DB *sqlx.DB

func Setup(dbPath string) {
	logging.Debug("connecting db '%s' ...", dbPath)
	conn, err := sqlx.Connect(dbType, dbPath)
	if err != nil {
		logging.Panic("failed to connect database")
	}
	DB = conn
	logging.Debug("connected")
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

	for _, scheme := range schemes {
		DB.MustExec(scheme)
	}
}
