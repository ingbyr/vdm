/*
 @Author: ingbyr
*/

package v1

import "C"
import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model/downloader"
	"github.com/ingbyr/vdm/pkg/e"
	"github.com/ingbyr/vdm/pkg/r"
)

func GetDownloaderManager(c *gin.Context) {
	r.OK(c, downloader.GetManager())
}

func FetchMediaInfo(c *gin.Context) {
	var taskConfig downloader.TaskConfig
	if err := c.BindJSON(&taskConfig); err != nil {
		r.Failed(c, e.InvalidParams, nil)
		return
	}
	task := downloader.NewTask(&taskConfig)
	formats, err := downloader.GetManager().FetchMediaInfo(task)
	if err != nil {
		r.Failed(c, e.Error, err)
		return
	}
	r.OK(c, formats)
}

func AddDownloadTask(c *gin.Context) {
	var taskConfig downloader.TaskConfig
	if err := c.BindJSON(&taskConfig); err != nil {
		r.Failed(c, e.InvalidParams, nil)
		return
	}
	task := downloader.NewTask(&taskConfig)
	err := downloader.GetManager().Download(task)
	if err != nil {
		r.Failed(c, e.Error, err.Error())
		return
	}
	r.OK(c, task)
}
