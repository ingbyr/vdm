package store

import (
	"gorm.io/gorm"
)

type Page struct {
	Size  int         `form:"size" json:"size"`
	Page  int         `form:"page" json:"page"`
	Total int64       `json:"total"`
	Data  interface{} `json:"data"`
}

func PagingQuery(tx *gorm.DB, page *Page) *Page {
	// max 100 item
	if page.Size > 100 {
		page.Size = 100
	}
	// total query
	if err := tx.Count(&page.Total).Error; err != nil {
		log.Panic("failed to count data", err)
	}
	// page query
	offset := (page.Page - 1) * page.Size
	tx.Offset(offset).Limit(page.Size)
	// TODO disable debug mode
	if err := tx.Debug().Find(page.Data).Error; err != nil {
		log.Panic("failed to query page", err)
	}
	return page
}
