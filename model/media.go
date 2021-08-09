/*
 @Author: ingbyr
*/

package model

type MediaInfo struct {
	*MediaBaseInfo
	Formats []*MediaFormat `json:"formats,omitempty"`
}

type MediaFormat struct {
	Format   string `json:"format,omitempty"`
	Id       string `json:"id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}

type MediaBaseInfo struct {
	Title string `json:"title" db:"title"`
	Desc  string `json:"description" db:"desc"`
}
