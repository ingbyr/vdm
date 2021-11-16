/*
 @Author: ingbyr
*/

package ytdl

import (
	"github.com/ingbyr/vdm/app/media"
	"github.com/ingbyr/vdm/app/task"
)

// MediaInfo from ytdl command output
type MediaInfo struct {
	Title     string         `json:"title,omitempty"`
	FullTitle string         `json:"fullTitle,omitempty"`
	Desc      string         `json:"description,omitempty"`
	Formats   []*MediaFormat `json:"formats,omitempty"`
}

// MediaFormat from ytdl command output
type MediaFormat struct {
	Format   string `json:"format,omitempty"`
	FormatId string `json:"format_id,omitempty"`
	Url      string `json:"url,omitempty"`
	Ext      string `json:"ext,omitempty"`
	FileSize int    `json:"fileSize,omitempty"`
}

// standardize convert media info of ytdl to standardized media format
func (m *MediaInfo) standardize(mtask *task.MTask) *media.Formats {
	formats := make([]media.Format, 0, len(m.Formats))
	for _, format := range m.Formats {
		formats = append(formats, media.Format{
			Format:   format.Format,
			Id:       format.FormatId,
			Url:      format.Url,
			Ext:      format.Ext,
			FileSize: format.FileSize,
		})
	}
	return &media.Formats{
		Info: &media.Info{
			Title: m.Title,
			Desc:  m.Desc,
			Url:   mtask.MediaUrl,
		},
		Formats: formats,
	}
}
