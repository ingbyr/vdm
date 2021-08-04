package ws

import (
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"github.com/ingbyr/vdm/pkg/logging"
	"net/http"
)

type Client struct {
	ID     string
	Socket *websocket.Conn
	Send   chan []byte
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

func (c *Client) Read() {
	defer func() {
		Manager.unregister <- c
		c.Socket.Close()
	}()
	for {
		c.Socket.PongHandler()
		_, msg, err := c.Socket.ReadMessage()
		if err != nil {
			Manager.unregister <- c
			c.Socket.Close()
			break
		}
		logging.Debug("Recv msg from Client %s: %s", c.ID, string(msg))
	}
}

func (c *Client) Write() {
	defer func() {
		c.Socket.Close()
	}()

	for {
		select {
		case msg, ok := <-c.Send:
			if !ok {
				c.Socket.WriteMessage(websocket.CloseMessage, []byte{})
				return
			}
			logging.Debug("Client %s send msg: %s", c.ID, string(msg))
			c.Socket.WriteMessage(websocket.TextMessage, msg)
		}
	}
}
