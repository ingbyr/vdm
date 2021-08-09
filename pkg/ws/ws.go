package ws

func Setup() {
	go startManager()
	go Heartbeat()
}
