package ws

func Setup() {
	go startManager()
	go heartbeat()
}
