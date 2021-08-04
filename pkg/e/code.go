/*
 @Author: ingbyr
 @Description:
*/

package e

const (
	Success       = 200
	Error         = 500
	InvalidParams = 400
	Unknown       = 999
)

var CodeMsg = map[int]string{
	Success:       "ok",
	Error:         "failed",
	InvalidParams: "invalid params",
	Unknown:       "unknown error code",
}

func GetMsg(code int) string {
	if msg, ok := CodeMsg[code]; ok {
		return msg
	}
	return CodeMsg[Unknown]
}
