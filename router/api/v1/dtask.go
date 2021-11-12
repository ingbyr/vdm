/*
 @Author: ingbyr
*/

package v1

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/app/page"
	"github.com/ingbyr/vdm/app/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetDownloadTasks(c *gin.Context) {
	dtask := &task.DTask{}
	if err := c.ShouldBindQuery(dtask); err != nil {
		log.Panic("get page failed: %v", err)
		r.FE(c, e.InvalidParams, err)
	}
	tx := db.DB.Model(dtask)
	if dtask.Media.Title != "" {
		tx.Where("title LIKE ?", "%"+dtask.Media.Title+"%")
		dtask.Media.Title = ""
	}
	if dtask.Media.Desc != "" {
		tx.Where("desc LIKE ?", "%"+dtask.Media.Desc+"%")
		dtask.Media.Desc = ""
	}
	tx.Where(dtask).Order("status DESC")
	p := page.Query(c, tx, &[]task.DTask{})
	r.OK(c, p)
}

