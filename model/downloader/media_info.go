/*
 @Author: ingbyr
*/

package downloader

type MediaInfo struct {
	Title     string         `json:"title,omitempty"`
	FullTitle string         `json:"fullTitle,omitempty"`
	Desc      string         `json:"description,omitempty"`
	Formats   []*MediaFormat `json:"formats,omitempty"`
}

type MediaFormat struct {
	Format   string `json:"format,omitempty"`
	Id       string `json:"id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}
