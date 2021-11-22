/*
 @Author: ingbyr
*/

package v1

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/app/engine"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetEngines(c *gin.Context) {
	r.OK(c, engine.Engines())
}

func GetMediaFormats(c *gin.Context) {
	mtask := new(task.MTask)
	if err := c.ShouldBindQuery(mtask); err != nil {
		r.FE(c, e.InvalidParams, err)
		return
	}
	res, err := engine.GetMediaFormats(mtask)
	if err != nil {
		r.FE(c, e.FetchMediaInfoError, err)
		return
	}
	r.OK(c, res)
}

func DownloadMedia(c *gin.Context) {
	dtask := task.NewDTask()
	if err := c.ShouldBindJSON(dtask); err != nil {
		r.FE(c, e.InvalidParams, err)
		return
	}
	if err := engine.DownloadMedia(dtask); err != nil {
		r.FE(c, e.DownloadMediaError, err)
		return
	}
	r.OK(c, dtask)
}
