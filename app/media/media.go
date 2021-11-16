/*
 @Author: ingbyr
*/

package media

type Info struct {
	Title string `json:"title"  form:"title" binding:"required"`
	Desc  string `json:"desc" form:"desc" binding:"required"`
	Url   string `json:"url" form:"url" binding:"required"`
}

type Formats struct {
	*Info
	Formats []Format `json:"formats,omitempty" form:"formats" gorm:"-"`
}

type Format struct {
	Format   string `json:"format,omitempty"`
	Id       string `json:"id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}

type Selection struct {
	*Info    `gorm:"embedded"`
	FormatId string `json:"formatId" form:"formatId" binding:"required"`
}
