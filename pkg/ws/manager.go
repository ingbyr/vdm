package ws

import (
	"encoding/json"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/setting"
	"time"
)

type manager struct {
	clients       map[string]*Client
	register      chan *Client
	unregister    chan *Client
	broadcast     chan []byte
	heartbeatData map[string]interface{}
}

var Manager = manager{
	clients:       make(map[string]*Client),
	register:      make(chan *Client),
	unregister:    make(chan *Client),
	broadcast:     make(chan []byte),
	heartbeatData: make(map[string]interface{}),
}

func startManager() {
	for {
		select {
		case conn := <-Manager.register:
			logging.Debug("Client %s joined", conn.ID)
			Manager.clients[conn.ID] = conn
			synMsg, _ := json.Marshal(&Message{Content: "Successful connection to VDM"})
			conn.Send <- synMsg
		case conn := <-Manager.unregister:
			logging.Debug("Client %s left", conn.ID)
			if _, ok := Manager.clients[conn.ID]; ok {
				//finMsg, _ := json.Marshal(&Message{Content: "A socket has disconnected"})
				//conn.Send <- finMsg
				close(conn.Send)
				delete(Manager.clients, conn.ID)
			}
		case msg := <-Manager.broadcast:
			for _, c := range Manager.clients {
				select {
				case c.Send <- msg:
				default:
					close(c.Send)
					delete(Manager.clients, c.ID)
				}
			}
		}
	}
}

func Register(client *Client) {
	Manager.register <- client
	go client.Read()
	go client.Write()
}

func SendBroadcast(msg []byte) {
	if len(Manager.clients) == 0 {
		logging.Debug("no client to receive msg")
		return
	}
	logging.Debug("send broadcast msg: %s", string(msg))
	Manager.broadcast <- msg
}

func heartbeat() {
	ticker := time.NewTicker(setting.AppSetting.HeartbeatInterval * time.Second)
	defer ticker.Stop()
	for range ticker.C {
		data, _ := json.Marshal(Manager.heartbeatData)
		SendBroadcast(data)
	}
}

func UpdateHeartbeatData(id string, data interface{}) {
	Manager.heartbeatData[id] = data
}
