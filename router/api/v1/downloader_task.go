/*
 @Author: ingbyr
*/

package v1

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/r"
)

var log = logging.New("v1")

func AddDownloadTask(c *gin.Context) {
	var taskConfig model.DTaskConfig
	if err := c.BindJSON(&taskConfig); err != nil {
		r.Failed(c, e.InvalidParams)
		return
	}
	task := model.NewDTask(&taskConfig)
	err := model.DecManager.Download(task)
	if err != nil {
		r.Failed(c, e.Error)
		return
	}
	// save to database
	db.DB.Create(task)
	r.OK(c, task)
}

func GetDownloaderTask(c *gin.Context) {
	task := &model.DTask{}
	if err := c.ShouldBindQuery(task); err != nil {
		log.Panic("get task page failed: %v", err)
	}
	tx := db.DB.Model(task)
	if task.Title != "" {
		tx.Where("title LIKE ?", "%"+task.Title+"%")
		task.Title = ""
	}
	if task.Desc != "" {
		tx.Where("desc LIKE ?", "%"+task.Desc+"%")
		task.Desc = ""
	}
	tx.Where(task).Order("status DESC")
	page := model.PageQuery(c, tx, &[]model.DTask{})
	r.OK(c, page)
}
