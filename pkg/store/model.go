/*
 @Author: ingbyr
*/

package store

import (
	"database/sql/driver"
	"fmt"
	"github.com/bwmarrin/snowflake"
	"github.com/ingbyr/vdm/pkg/uuid"
	"time"
)

// Model is a base database model
type Model struct {
	ID        snowflake.ID `json:"id" gorm:"primaryKey" form:"id"`
	CreatedAt *LocalTime   `json:"createTime" gorm:"embedded column:created_at" form:"created_at"`
	UpdatedAt *LocalTime   `json:"updateTime" gorm:"embedded column:updated_at" form:"updated_at"`
}

func NewModel() *Model {
	return &Model{
		ID:        uuid.Instance.Generate(),
		CreatedAt: &LocalTime{time.Now()},
		UpdatedAt: &LocalTime{time.Now()},
	}
}

// LocalTime provide a time which can be formatted in json and database
type LocalTime struct {
	time.Time
}

func (t *LocalTime) MarshalJSON() ([]byte, error) {
	return []byte(fmt.Sprintf(`"%s"`, t.Format("2006-01-02 15:04:05"))), nil
}

func (t *LocalTime) UnmarshalJSON(data []byte) error {
	var err error
	if t.Time, err = time.Parse(`"2006-01-02 15:04:05"`, string(data)); err != nil {
		return err
	}
	return nil
}

func (t *LocalTime) Value() (driver.Value, error) {
	var zeroTime time.Time
	if t.Time.UnixNano() == zeroTime.UnixNano() {
		return nil, nil
	}
	return t.Time, nil
}

func (t *LocalTime) Scan(v interface{}) error {
	value, ok := v.(time.Time)
	if ok {
		*t = LocalTime{Time: value}
		return nil
	}
	return fmt.Errorf("can not convert %v to timestamp", v)
}
