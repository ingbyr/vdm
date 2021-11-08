/*
 @Author: ingbyr
*/

package v1

import "C"
import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetDownloaderInfo(c *gin.Context) {
	r.OK(c, model.DecManager.Downloaders)
}

func FetchMediaInfo(c *gin.Context) {
	var taskConfig model.DTaskConfig
	if err := c.BindJSON(&taskConfig); err != nil {
		r.Failed(c, e.InvalidParams)
		return
	}
	task := model.NewDTask(&taskConfig)
	formats, errC := model.DecManager.FetchMediaInfo(task)
	if errC != e.Ok {
		r.Failed(c, errC)
		return
	}
	r.OK(c, formats)
}
