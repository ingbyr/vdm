/*
 @Author: ingbyr
*/

package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model/engine"
	"github.com/ingbyr/vdm/model/schema"
	"github.com/ingbyr/vdm/pkg/db"
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
	db.Setup()
	engine.Setup(ctx, cancel)
	schema.Setup()
	return ctx, cancel
}

func run() {
	_, cancel := setup()
	gin.SetMode(setting.ServerSetting.RunMode)
	handler := router.Init()
	readTimeout := setting.ServerSetting.ReadTimeout
	writeTimeout := setting.ServerSetting.WriteTimeout
	endPoint := fmt.Sprintf(":%d", setting.ServerSetting.HttpPort)
	maxHeaderBytes := 1 << 20

	srv := &http.Server{
		Addr:           endPoint,
		Handler:        handler,
		ReadTimeout:    readTimeout,
		WriteTimeout:   writeTimeout,
		MaxHeaderBytes: maxHeaderBytes,
	}

	// start vdm server
	go func() {
		log.Infow("start server", "port", endPoint)
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
	log.Info("shutting down vdm...")

	// stop running goroutines
	cancel()

	// stop after 1 second
	ctxWait, cancelWait := context.WithTimeout(context.Background(), time.Second)
	defer cancelWait()

	if err := srv.Shutdown(ctxWait); err != nil {
		log.Panic("vdm forced to shutdown:", err)
	}

	log.Info("exit")
}

func main() {
	run()
}
