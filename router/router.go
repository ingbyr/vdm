/*
 @Author: ingbyr
*/

package router

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	v1 "github.com/ingbyr/vdm/router/api/v1"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

func Init() *gin.Engine {
	r := gin.New()
	//logger := logging.Gin()
	//r.Use(ginzap.Ginzap(logger, time.RFC3339, true))
	//r.Use(ginzap.RecoveryWithZap(logger, true))
	r.Use(cors.Default())
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	initWsRouter(r)
	initApiV1(r)
	return r
}

func initWsRouter(r *gin.Engine) {
	wsGroup := r.Group("/ws")
	wsGroup.GET("/connect", WsConnect)
	wsGroup.GET("/send", WsSendMsg)
}

func initApiV1(r *gin.Engine) {
	// Api group
	apiGroup := r.Group("/api")

	// V1 api group
	apiV1 := apiGroup.Group("/v1")

	// V1 engine api group
	engineApi := apiV1.Group("/downloader")
	{
		engineApi.GET("/info", v1.GetDownloaderInfo)
		engineApi.POST("/media/info", v1.FetchMediaInfo)
		engineApi.POST("/download", v1.AddDownloadTask)
		engineApi.GET("/task", v1.GetDownloaderTask)
	}
}
