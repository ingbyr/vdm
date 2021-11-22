package ws

import (
	"context"
	"encoding/json"
)

type manager struct {
	clients    map[string]*Client
	register   chan *Client
	unregister chan *Client
	broadcast  chan []byte
}

var Manager = manager{
	clients:    make(map[string]*Client),
	register:   make(chan *Client),
	unregister: make(chan *Client),
	broadcast:  make(chan []byte, 1<<12),
}

func startWebsocket(ctx context.Context) {
	for {
		select {
		case conn := <-Manager.register:
			log.Debugw("client joined", "client", conn.ID)
			conn.Ctx, conn.Cancel = context.WithCancel(ctx)
			go conn.ReadLoop()
			go conn.WriteLoop()
			Manager.clients[conn.ID] = conn
			synMsg, _ := json.Marshal(&Message{Content: "connected"})
			conn.Send <- synMsg
		case conn := <-Manager.unregister:
			log.Debugw("client left", "client", conn.ID)
			if _, ok := Manager.clients[conn.ID]; ok {
				close(conn.Send)
				delete(Manager.clients, conn.ID)
			}
		case msg := <-Manager.broadcast:
			if len(Manager.clients) == 0 {
				continue
			}
			for _, c := range Manager.clients {
				select {
				case c.Send <- msg:
				default:
					close(c.Send)
					delete(Manager.clients, c.ID)
				}
			}
		case <-ctx.Done():
			return
		}
	}
}

func Register(client *Client) {
	Manager.register <- client
}

func Broadcast(msg []byte) {
	if len(Manager.clients) == 0 {
		return
	}
	Manager.broadcast <- msg
}
