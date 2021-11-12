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
	//heartbeatData map[string]map[string]interface{}
}

var Manager = manager{
	clients:    make(map[string]*Client),
	register:   make(chan *Client),
	unregister: make(chan *Client),
	broadcast:  make(chan []byte, 1024),
	// FIXME map causes memory leaking
	//heartbeatData: make(map[string]map[string]interface{}),

}

func startWebsocket(ctx context.Context) {
	for {
		select {
		case conn := <-Manager.register:
			log.Debug("client %s joined", conn.ID)
			Manager.clients[conn.ID] = conn
			synMsg, _ := json.Marshal(&Message{Content: "successful connection to vdm"})
			conn.Send <- synMsg
		case conn := <-Manager.unregister:
			log.Debug("client %s left", conn.ID)
			if _, ok := Manager.clients[conn.ID]; ok {
				close(conn.Send)
				delete(Manager.clients, conn.ID)
			}
		case msg := <-Manager.broadcast:
			if len(Manager.clients) == 0 {
				continue
			}
			log.Debugw("broadcast msg", "size", len(msg))
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
	go client.Read()
	go client.Write()
}

func Broadcast(msg []byte) {
	if len(Manager.clients) == 0 {
		return
	}
	Manager.broadcast <- msg
}

//func Heartbeat(ctx context.Context) {
//	ticker := time.NewTicker(setting.AppSetting.HeartbeatInterval * time.Second)
//	defer ticker.Stop()
//	for {
//		select {
//		case <-ticker.C:
//			InvokeHeartbeat()
//		case <-ctx.Done():
//			return
//		}
//	}
//}

//func InvokeHeartbeat() {
//	data, _ := json.Marshal(Manager.heartbeatData)
//	Broadcast(data)
//}
//
//func AppendHeartbeatData(group string, id string, data interface{}) {
//	if _, ok := Manager.heartbeatData[group]; !ok {
//		log.Debug("create heartbeat group: %s", group)
//		Manager.heartbeatData[group] = make(map[string]interface{})
//	}
//	Manager.heartbeatData[group][id] = data
//}
//
//func RemoveHeartbeatData(group string, id interface{}) {
//	if _, ok := Manager.heartbeatData[group]; !ok {
//		return
//	}
//	// must send once before removing
//	InvokeHeartbeat()
//	delete(Manager.heartbeatData[group], fmt.Sprintf("%v", id))
//}
