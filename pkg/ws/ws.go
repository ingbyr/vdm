package ws

import "context"

func Setup(ctx context.Context) {
	go startManager(ctx)
	go Heartbeat(ctx)
}
