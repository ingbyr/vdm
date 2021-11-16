/*
 @Author: ingbyr
*/

package v1

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
	"github.com/ingbyr/vdm/pkg/store"
)

func GetDownloadTasks(c *gin.Context) {
	dtask := &task.DTask{}
	if err := c.ShouldBindQuery(dtask); err != nil {
		r.FE(c, e.InvalidParams, err)
		return
	}
	p := &store.Page{}
	if err := c.ShouldBindQuery(p); err != nil {
		r.FE(c, e.InvalidUrl, err)
		return
	}
	r.OK(c, dtask.GetDTasks(p))
}
