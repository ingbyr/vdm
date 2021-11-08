/*
 @Author: ingbyr
*/

package media

type Info struct {
	Base
	Formats []*Format `json:"formats,omitempty"`
}

type Base struct {
	Title string `json:"title" db:"title" form:"title"`
	Desc  string `json:"description" db:"desc" form:"desc"`
}

type Format struct {
	Format   string `json:"format,omitempty"`
	Id       string `json:"id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
