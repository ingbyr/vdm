package router

import (
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/pkg/ws"
	"net/http"
)

func WsConnect(c *gin.Context) {
	uid := c.Query("uid")
	client := ws.NewClient(uid, c)
	ws.Register(client)
}

func WsSendMsg(c *gin.Context) {
	msg := c.Query("msg")
	ws.SendBroadcast([]byte(msg))
	c.String(http.StatusOK, "vdm-ws send %s", msg)
}
