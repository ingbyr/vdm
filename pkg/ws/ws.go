package ws

func init() {
	go startManager()
	go heartbeat()
}
