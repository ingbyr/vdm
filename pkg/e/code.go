/*
 @Author: ingbyr
 @Description:
*/

package e

const (
	Ok    = 200
	Error = 500
	InvalidParams = 400
	Unknown       = 999

	JsonNonDeserializable = 1001

	DownloaderUnavailable  = 2000
	DownloaderNotValidUrl = 2001
	DownloaderUnknown     = 2999
)

var CodeMsg = map[uint]string{
	Ok:            "ok",
	Error:         "failed",
	InvalidParams: "invalid params",
	Unknown:       "unknown error",

	JsonNonDeserializable: "can not deserialize json bytes",

	DownloaderUnavailable: "downloader not found or is disabled",
	DownloaderNotValidUrl: "url is not valid",
	DownloaderUnknown:     "unknown error for downloader",
}

func ToMsg(code uint) string {
	if msg, ok := CodeMsg[code]; ok {
		return msg
	}
	return CodeMsg[Unknown]
}
