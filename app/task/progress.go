/*
 @Author: ingbyr
*/

package task

import (
	"github.com/bwmarrin/snowflake"
	"github.com/ingbyr/vdm/pkg/store"
)

type status = int

const (
	Created status = iota + 1
	Downloading
	Merging
	Paused
	Completed
	Failed
)

type Progress struct {
	ID        snowflake.ID `json:"id" gorm:"id"`
	Status    status       `json:"status" gorm:"status"`
	CmdOutput string       `json:"cmdOutput" gorm:"cmd_output"`
	Percent   string       `json:"progress" gorm:"percent"`
	Speed     string       `json:"speed" gorm:"speed"`
}

func (p Progress) Save() {
	store.DB.Model(p).Updates(p)
}
