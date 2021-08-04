/*
 @Author: ingbyr
*/

package downloader

type MediaInfo interface {
	GetTitle() string
	GetFullTitle() string
	GetDesc() string
	GetFormats() interface{}
}

//type MediaFormat interface {
//	GetId() string
//	GetExt() string
//	GetSize() string
//}
