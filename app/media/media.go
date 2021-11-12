/*
 @Author: ingbyr
*/

package media

type Media struct {
	Title   string   `json:"title"  form:"title"`
	Desc    string   `json:"description" form:"desc"`
	Formats []Format `json:"formats,omitempty" gorm:"-"`
}

type Format struct {
	Format   string `json:"format,omitempty"`
	Id       string `json:"id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
