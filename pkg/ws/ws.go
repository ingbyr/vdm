package ws

import "context"

func Setup(ctx context.Context) {
	go startManager(ctx)
}
