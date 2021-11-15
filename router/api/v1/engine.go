/*
 @Author: ingbyr
*/

package v1

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/app/engine"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetEngines(c *gin.Context) {
	r.OK(c, engine.Engines())
}

func FetchMediaInfo(c *gin.Context) {
	mtask := new(task.MTask)
	if err := c.BindJSON(mtask); err != nil {
		r.FE(c, e.InvalidParams, err)
		return
	}
	res, err := engine.FetchMediaInfo(mtask)
	if err != nil {
		r.FE(c, e.FetchMediaInfoError, err)
		return
	}
	r.OK(c, res)
}

func DownloadMedia(c *gin.Context) {
	dtask := task.NewDTask()
	if err := c.BindJSON(dtask); err != nil {
		r.FE(c, e.InvalidParams, err)
		return
	}
	if err := engine.DownloadMedia(dtask); err != nil {
		log.Error(err)
		r.FE(c, e.DownloadMediaError, err)
		return
	}
	// save to database
	db.DB.Create(dtask)
	r.OK(c, dtask)
}
