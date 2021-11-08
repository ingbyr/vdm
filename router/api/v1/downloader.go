/*
 @Author: ingbyr
*/

package v1

import "C"
import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model/engine"
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetDownloaderInfo(c *gin.Context) {
	r.OK(c, engine.Manager.Engines)
}

func FetchMediaInfo(c *gin.Context) {
	mtask := new(task.MTask)
	if err := c.BindJSON(mtask); err != nil {
		r.F(c, e.InvalidParams)
		return
	}
	res, err := engine.Manager.FetchMediaInfo(mtask)
	if err != nil {
		r.FE(c, e.EngineExecError, err)
		return
	}
	r.OK(c, res)
}
