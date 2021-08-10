/*
 @Author: ingbyr
*/

package model

import (
	"database/sql/driver"
	"fmt"
	"github.com/bwmarrin/snowflake"
	"github.com/ingbyr/vdm/pkg/uuid"
	"time"
)

const (
	TimeFormat = "2006-01-02 15:04:05"
)

type Model struct {
	ID        snowflake.ID `json:"id" gorm:"primaryKey" form:"id"`
	CreatedAt JsonTime     `json:"createTime" gorm:"column:created_at" form:"created_at"`
	UpdatedAt JsonTime     `json:"updateTime" gorm:"column:updated_at" form:"updated_at"`
}

func NewModel() Model {
	return Model{
		ID:        uuid.Instance.Generate(),
		CreatedAt: JsonTime{time.Now()},
		UpdatedAt: JsonTime{time.Now()},
	}
}

type JsonTime struct {
	time.Time
}

func (t *JsonTime) MarshalJSON() ([]byte, error) {
	return []byte(fmt.Sprintf(`"%s"`, t.Format(TimeFormat))), nil
}

func (t *JsonTime) UnmarshalJSON(data []byte) error {
	var err error
	if t.Time, err = time.Parse(TimeFormat, string(data)); err != nil {
		return err
	}
	return nil
}

func (t JsonTime) Value() (driver.Value, error) {
	var zeroTime time.Time
	if t.Time.UnixNano() == zeroTime.UnixNano() {
		return nil, nil
	}
	return t.Time, nil
}

func (t *JsonTime) Scan(v interface{}) error {
	value, ok := v.(time.Time)
	if ok {
		*t = JsonTime{Time: value}
		return nil
	}
	return fmt.Errorf("can not convert %v to timestamp", v)
}
