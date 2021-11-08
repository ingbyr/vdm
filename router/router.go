/*
 @Author: ingbyr
*/

package router

import (
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	v1 "github.com/ingbyr/vdm/router/api/v1"
)

func Init() *gin.Engine {
	r := gin.New()
	//logger := logging.Gin()
	//r.Use(ginzap.Ginzap(logger, time.RFC3339, true))
	//r.Use(ginzap.RecoveryWithZap(logger, true))
	r.Use(cors.Default())
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
	apiGroup := r.Group("/api")
	apiv1 := apiGroup.Group("/apiv1")

	apiv1.GET("/engine", v1.GetEngineInfo)
	apiv1.POST("/media/info/fetch", v1.FetchMediaInfo)
	apiv1.POST("/media/download", v1.DownloadMedia)
	apiv1.GET("/task", v1.GetDownloadTasks)
}
