/*
 @Author: ingbyr
*/

package v1

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model/engine"
	"github.com/ingbyr/vdm/model/page"
	"github.com/ingbyr/vdm/model/task"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/r"
)

var log = logging.New("v1")

func AddDownloadTask(c *gin.Context) {
	var taskConfig task.Config
	if err := c.BindJSON(&taskConfig); err != nil {
		r.F(c, e.InvalidParams)
		return
	}
	t := task.NewDTask(taskConfig)
	err := engine.Manager.Download(t)
	if err != nil {
		r.F(c, e.Error)
		return
	}
	// save to database
	db.DB.Create(t)
	r.OK(c, t)
}

func GetDownloaderTask(c *gin.Context) {
	t := &task.DTask{}
	if err := c.ShouldBindQuery(t); err != nil {
		log.Panic("get page failed: %v", err)
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
