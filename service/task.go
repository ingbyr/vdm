package service

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/logging"
)

func GetTaskPage(c *gin.Context) *model.Page {
	task := &model.DownloaderTask{}
	if err := c.ShouldBindQuery(task); err != nil {
		logging.Panic("get task page failed: %v", err)
	}
	tx := db.DB.Model(task)
	return model.PageQuery(c, tx, &[]model.DownloaderTask{})
}
