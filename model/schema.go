/*
 @Author: ingbyr
*/

package model

import "github.com/ingbyr/vdm/pkg/db"

func SetupSchema() {
	db.DB.AutoMigrate(DownloaderTask{})
}
