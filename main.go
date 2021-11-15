/*
 @Author: ingbyr
*/

package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/app/engine"
	_ "github.com/ingbyr/vdm/app/engines"
	"github.com/ingbyr/vdm/app/schema"
	"github.com/ingbyr/vdm/pkg/store"
	"github.com/ingbyr/vdm/pkg/logging"
	"github.com/ingbyr/vdm/pkg/setting"
	"github.com/ingbyr/vdm/pkg/ws"
	"github.com/ingbyr/vdm/router"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"
)

var log = logging.New("server")

func setup() (context.Context, context.CancelFunc) {
	ctx, cancel := context.WithCancel(context.Background())
	setting.Setup()
	ws.Setup(ctx)
	store.Setup()
	engine.Setup(ctx)
	schema.Setup()
	gin.SetMode(setting.ServerSetting.RunMode)
	logging.SetLevel(setting.LogSetting.Level)
	return ctx, cancel
}

func run() {
	ctx, cancel := setup()

	srv := &http.Server{
		Addr:           fmt.Sprintf(":%d", setting.ServerSetting.HttpPort),
		Handler:        router.Init(),
		ReadTimeout:    setting.ServerSetting.ReadTimeout,
		WriteTimeout:   setting.ServerSetting.WriteTimeout,
		MaxHeaderBytes: 1 << 20,
	}

	// start vdm server
	go func() {
		log.Infow("start server", "port", srv.Addr)
		if err := srv.ListenAndServe(); err != nil {
			if !errors.Is(err, http.ErrServerClosed) {
				log.Panic("server error", err)
			}
		}
	}()

	// listen exit signal
	quit := make(chan os.Signal)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	log.Info("shutting down")

	// stop running goroutines
	cancel()
	<-ctx.Done()

	// stop in 1 second
	ctxWait, cancelWait := context.WithTimeout(context.Background(), time.Second)
	defer cancelWait()

	if err := srv.Shutdown(ctxWait); err != nil {
		log.Panic("forced to shutdown:", err)
	}

	log.Info("exit")
}

func main() {
	run()
}
