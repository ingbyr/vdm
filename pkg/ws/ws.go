package ws

func _init() {
	go startManager()
	go heartbeat()
}
