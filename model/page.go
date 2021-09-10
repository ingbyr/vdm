package model

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/pkg/logging"
	"gorm.io/gorm"
)

type Page struct {
	Size  int         `form:"size" json:"size"`
	Page  int         `form:"page" json:"page"`
	Total int64       `json:"total"`
	Data  interface{} `json:"data"`
}

func PageQuery(c *gin.Context, tx *gorm.DB, target interface{}) *Page {
	page := &Page{}
	page.Data = target
	if err := c.ShouldBindQuery(page); err != nil {
		logging.Panic("failed to parse page query args: %v", err)
	}
	// max 100 item
	if page.Size > 100 {
		page.Size = 100
	}
	// total query
	if err := tx.Count(&page.Total).Error; err != nil {
		logging.Panic("failed to count data: %v", err)
	}
	// page query
	offset := (page.Page - 1) * page.Size
	tx.Offset(offset).Limit(page.Size)
	if err := tx.Find(page.Data).Error; err != nil {
		logging.Panic("failed to query page: %v", err)
	}
	return page
}
