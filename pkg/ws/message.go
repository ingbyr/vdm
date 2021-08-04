package ws

type Message struct {
	Sender  string `json:"sender,omitempty"`
	Content string `json:"content,omitempty"`
}
