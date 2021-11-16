/*
 @Author: ingbyr
 @Description:
*/

package r

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/pkg/e"
	"net/http"
)

type Response struct {
	Code e.Code      `json:"code"`
	Msg  string      `json:"msg"`
	Data interface{} `json:"data"`
}

func OK(c *gin.Context, data interface{}) {
	R(c, http.StatusOK, e.Ok, data)
}

func FE(c *gin.Context, errCode e.Code, err error) {
	R(c, http.StatusOK, errCode, err.Error())
}

func R(c *gin.Context, httpCode int, code e.Code, data interface{}) {
	c.JSON(httpCode, Response{
		Code: code,
		Msg:  code.String(),
		Data: data,
	})
}
