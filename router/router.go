/*
 @Author: ingbyr
*/

package router

import (
	"context"
	"github.com/gin-contrib/cors"
	ginzap "github.com/gin-contrib/zap"
	"github.com/gin-gonic/gin"
	"github.com/ingbyr/vdm/model/downloader"
	"github.com/ingbyr/vdm/pkg/logging"
	v1 "github.com/ingbyr/vdm/router/api/v1"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"time"
)

func Init(ctx context.Context) *gin.Engine {
	initCtx(ctx)
	r := initGin()
	initApiV1(r)
	return r
}

func initCtx(ctx context.Context) {
	downloader.Init(ctx)
}

func initGin() *gin.Engine {
	r := gin.New()
	r.Use(ginzap.Ginzap(logging.GinLogger, time.RFC3339, true))
	r.Use(ginzap.RecoveryWithZap(logging.GinLogger, true))
	r.Use(cors.Default())
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	return r
}

func initApiV1(r *gin.Engine) {
	// Api group
	apiGroup := r.Group("/api")

	// V1 api group
	apiV1 := apiGroup.Group("/v1")

	// V1 engine api group
	engineApi := apiV1.Group("/downloader")
	{
		engineApi.GET("/manager", v1.GetDownloaderManager)
		engineApi.POST("/media-info/fetch", v1.FetchMediaInfo)
		engineApi.POST("/task", v1.AddDownloadTask)
	}

	// V1 websocket api group
	wsApi := apiV1.Group("/ws")
	{
		wsApi.GET("/connect", v1.WsConnect)
		wsApi.GET("/send", v1.WsSendMsg)
	}
}
