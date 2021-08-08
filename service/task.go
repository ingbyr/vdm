package service

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/db"
)

func GetTaskPage(c *gin.Context) []model.DownloaderTask {
	tasks := make([]model.DownloaderTask, 0)
	db.DB.Scopes(db.Page(c)).Find(&tasks)
	return tasks
}
