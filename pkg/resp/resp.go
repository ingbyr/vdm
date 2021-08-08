/*
 @Author: ingbyr
 @Description:
*/

package resp

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/pkg/e"
	"net/http"
)

type Response struct {
	Code int         `json:"code"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data"`
}

func OK(c *gin.Context, data interface{}) {
	R(c, http.StatusOK, e.Success, data)
}

func Failed(c *gin.Context, errorCode int, msg interface{}) {
	if msg == nil {
		msg = e.GetMsg(errorCode)
	}
	R(c, http.StatusOK, errorCode, msg)
}

func R(c *gin.Context, httpCode, errCode int, data interface{}) {
	c.JSON(httpCode, Response{
		Code: errCode,
		Msg:  e.GetMsg(errCode),
		Data: data,
	})
}
