/*
 @Author: ingbyr
*/

package v1

import "C"
import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model/engine"
	"github.com/ingbyr/vdm/model/page"
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetEngines(c *gin.Context) {
	r.OK(c, engine.Manager.Engines)
}

func FetchMediaInfo(c *gin.Context) {
	mtask := new(task.MTask)
	if err := c.BindJSON(mtask); err != nil {
		r.F(c, e.InvalidParams)
		return
	}
	// TODO set timeout
	res, err := engine.Manager.FetchMediaInfo(mtask)
	if err != nil {
		r.FE(c, e.FetchMediaInfoError, err)
		return
	}
	r.OK(c, res)
}

func DownloadMedia(c *gin.Context) {
	var taskConfig task.Config
	if err := c.BindJSON(&taskConfig); err != nil {
		r.F(c, e.InvalidParams)
		return
	}
	t := task.NewDTask(taskConfig)
	err := engine.Manager.Download(t)
	if err != nil {
		r.F(c, e.DownloadMediaError)
		return
	}
	// save to database
	db.DB.Create(t)
	r.OK(c, t)
}

func GetDownloadTasks(c *gin.Context) {
	t := &task.DTask{}
	if err := c.ShouldBindQuery(t); err != nil {
		log.Panic("get page failed: %v", err)
		r.FE(c, e.InvalidParams, err)
	}
	tx := db.DB.Model(t)
	if t.Title != "" {
		tx.Where("title LIKE ?", "%"+t.Title+"%")
		t.Title = ""
	}
	if t.Desc != "" {
		tx.Where("desc LIKE ?", "%"+t.Desc+"%")
		t.Desc = ""
	}
	tx.Where(t).Order("status DESC")
	p := page.Query(c, tx, &[]task.DTask{})
	r.OK(c, p)
}
