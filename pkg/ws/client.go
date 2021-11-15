package ws

import (
	"context"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"github.com/ingbyr/vdm/pkg/logging"
	"net/http"
)

var log = logging.New("ws")

type Client struct {
	ID     string
	Socket *websocket.Conn
	Send   chan []byte
	Ctx    context.Context
	Cancel context.CancelFunc
}

func NewClient(uid string, c *gin.Context) *Client {
	upgrader := websocket.Upgrader{CheckOrigin: func(r *http.Request) bool { return true }}
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		http.NotFound(c.Writer, c.Request)
		return nil
	}
	return &Client{
		ID:     uid,
		Socket: conn,
		Send:   make(chan []byte),
	}
}

func (c *Client) ReadLoop() {
	defer func() {
		Manager.unregister <- c
		c.Socket.Close()
	}()
	for {
		select {
		case <-c.Ctx.Done():
			return
		default:
			c.Socket.PongHandler()
			_, msg, err := c.Socket.ReadMessage()
			if err != nil {
				return
			}
			log.Debugw("receive msg", "client", c.ID, "msg", string(msg))
		}
	}
}

func (c *Client) WriteLoop() {
	defer func() {
		Manager.unregister <- c
		c.Socket.Close()
	}()

	for {
		select {
		case <-c.Ctx.Done():
			return
		case msg, ok := <-c.Send:
			if !ok {
				_ = c.Socket.WriteMessage(websocket.CloseMessage, []byte{})
				return
			}
			log.Debugw("send msg", "client", c.ID, "msg", string(msg))
			if err := c.Socket.WriteMessage(websocket.TextMessage, msg); err != nil {
				return
			}
		}
	}
}
