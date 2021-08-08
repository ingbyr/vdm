package service

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/db"
	"github.com/ingbyr/vdm/pkg/logging"
)

func GetTaskPage(c *gin.Context) []model.DownloaderTask {
	task := &model.DownloaderTask{}
	if err := c.ShouldBindQuery(task); err != nil {
		logging.Panic("get task page failed: %v", err)
	}
	tx := db.DB.Model(task)
	tasks := make([]model.DownloaderTask, 0)
	model.PageQuery(c, tx, &tasks)
	return tasks
}
