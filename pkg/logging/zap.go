/*
 @Author: ingbyr
*/

package logging

import (
	"go.uber.org/zap"
	"moul.io/zapgorm2"
)

var LoggerLevel zap.AtomicLevel

var baseLog *zap.Logger
var sugaredLog *zap.SugaredLogger

func init() {
	var err error
	baseLog, err = zap.NewProduction(zap.AddCaller())
	if err != nil {
		panic(err)
	}
	defer baseLog.Sync()
	sugaredLog = baseLog.Sugar()
}

func Gin() *zap.Logger {
	return baseLog.Named("gin")
}

func Gorm() zapgorm2.Logger {
	return zapgorm2.New(baseLog.Named("db"))
}

func New(name string) *zap.SugaredLogger {
	return sugaredLog.Named(name)
}
