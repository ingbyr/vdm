package service

import (
	"github.com/ingbyr/vdm/model"
	"github.com/ingbyr/vdm/pkg/db"
)

func AddDownloaderTask(taskConfig *model.DownloaderTaskConfig) (*model.DownloaderTask, error) {
	task := model.NewDownloaderTask(taskConfig)
	err := model.DownloaderManager.Download(task)
	if err != nil {
		return nil, err
	}
	// save to database
	db.DB.Create(task)
	return task, nil
}
