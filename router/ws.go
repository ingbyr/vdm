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

func WsBroadcast(c *gin.Context) {
	msg := c.Query("msg")
	ws.Broadcast([]byte(msg))
	c.String(http.StatusOK, "ws broadcast %s", msg)
}
