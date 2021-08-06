/*
 @Author: ingbyr
*/

package main

import (
	"context"
	"errors"
	"fmt"
	"github.com/gin-gonic/gin"
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

func setup() {
	setting.Setup()
	ws.Setup()
}

func run() {
	setup()
	gin.SetMode(setting.ServerSetting.RunMode)
	ctx, cancel := context.WithCancel(context.Background())
	handler := router.Setup(ctx)
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
		logging.Info("start vdm listening %s", endPoint)
		if err := srv.ListenAndServe(); err != nil && errors.Is(err, http.ErrServerClosed) {
			logging.Info("listen: %s\n", err)
		}
	}()

	// listen exit signal
	quit := make(chan os.Signal)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	logging.Info("shutting down vdm...")

	// stop running goroutines
	cancel()

	// stop after 1 second
	ctxWait, cancelWait := context.WithTimeout(context.Background(), time.Second)
	defer cancelWait()

	if err := srv.Shutdown(ctxWait); err != nil {
		logging.Panic("vdm forced to shutdown:", err)
	}

	logging.Info("vdm exited")
}

func main() {
	run()
}
