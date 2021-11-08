//go:generate stringer -type=Code -linecomment

/*
 @Author: ingbyr
*/

package e

type Code uint

const (
	Ok                  Code = iota // ok
	UnknownError                    // unknown error
	InvalidParams                   // params is not valid
	InvalidUrl                      // url is not valid
	UnavailableEngine               // engine is not available
	FetchMediaInfoError             // failed to fetch media info
	DownloadMediaError              // failed to download media
)
