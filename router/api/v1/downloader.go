/*
 @Author: ingbyr
*/

package v1

import "C"
import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/resp"
	"github.com/ingbyr/vdm/service"
)

func GetDownloaderInfo(c *gin.Context) {
	resp.OK(c, model.DownloaderManager.Downloaders)
}

func FetchMediaInfo(c *gin.Context) {
	var taskConfig model.DownloaderTaskConfig
	if err := c.BindJSON(&taskConfig); err != nil {
		resp.Failed(c, e.InvalidParams, nil)
		return
	}
	task := model.NewDownloaderTask(&taskConfig)
	formats, err := model.DownloaderManager.FetchMediaInfo(task)
	if err != nil {
		resp.Failed(c, e.Error, err)
		return
	}
	resp.OK(c, formats)
}

func AddDownloadTask(c *gin.Context) {
	var taskConfig model.DownloaderTaskConfig
	if err := c.BindJSON(&taskConfig); err != nil {
		resp.Failed(c, e.InvalidParams, err)
		return
	}
	res, err := service.AddDownloaderTask(&taskConfig)
	if err != nil {
		resp.Failed(c, e.Error, err.Error())
		return
	}
	resp.OK(c, res)
}

func GetDownloaderTask(c *gin.Context) {
	resp.OK(c, service.GetTaskPage(c))
}