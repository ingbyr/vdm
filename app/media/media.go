/*
 @Author: ingbyr
*/

package media

type Media struct {
	Title   string   `json:"title" db:"title" form:"title"`
	Desc    string   `json:"description" db:"desc" form:"desc"`
	Formats []Format `json:"formats,omitempty" db:"-"`
}

type Format struct {
	Format   string `json:"format,omitempty"`
	Id       string `json:"id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
