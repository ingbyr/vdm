/*
 @Author: ingbyr
*/

package task

import (
	"context"
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/pkg/store"
)

// DTask is a media downloading task
type DTask struct {
	// Base model
	*store.Model

	// Media format info
	*media.Info `json:"media" gorm:"embedded"`

	// FormatId is media format id from media.Format
	FormatId string `json:"formatId" form:"formatId"`

	// Engine is one of download engines
	Engine string `json:"engine" gorm:"engine" form:"engine"`

	// ExtArgs store the download engine command args from user input
	ExtArgs string `json:"extArgs" gorm:"ext_args"`

	// StoragePath is a media storage path
	StoragePath string `json:"storagePath" gorm:"storage_path" form:"storagePath"`

	// Progress will be updated in downloading operation
	Progress Progress `json:"progress" gorm:"foreignKey:ID"`

	Ctx    context.Context    `json:"-" gorm:"-"`
	Cancel context.CancelFunc `json:"-" gorm:"-"`
}

func NewDTask() *DTask {
	model := store.NewModel()
	return &DTask{
		Model: model,
		Info:  &media.Info{},
		Progress: Progress{
			ID: model.ID,
		},
	}
}

func (dtask *DTask) Save() {
	store.DB.Save(dtask)
}

func (dtask *DTask) Find(page *store.Page) *store.Page {
	page.Data = &[]DTask{}
	tx := store.DB.Model(dtask)
	if dtask.Title != "" {
		tx.Where("title LIKE ?", "%"+dtask.Title+"%")
		dtask.Title = ""
	}
	if dtask.Desc != "" {
		tx.Where("desc LIKE ?", "%"+dtask.Desc+"%")
		dtask.Desc = ""
	}

	tx.Where(dtask).Order("status DESC")
	return store.PagingQuery(tx, page)
}

func (dtask *DTask) FindSame(page *store.Page) *store.Page {
	query := DTask{
		Info:        dtask.Info,
		FormatId:    dtask.FormatId,
		Engine:      dtask.Engine,
		ExtArgs:     dtask.ExtArgs,
		StoragePath: dtask.StoragePath,
	}
	return query.Find(page)
}
