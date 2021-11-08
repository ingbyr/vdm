/*
 @Author: ingbyr
*/

package logging

import (
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"moul.io/zapgorm2"
	"os"
)

var LoggerLevel zap.AtomicLevel

var baseLog *zap.Logger
var sugaredLog *zap.SugaredLogger

func init() {
	LoggerLevel = zap.NewAtomicLevel()
	LoggerLevel.SetLevel(zapcore.DebugLevel)
	config := zap.NewProductionEncoderConfig()
	baseLog = zap.New(zapcore.NewCore(
		zapcore.NewJSONEncoder(config),
		zapcore.Lock(os.Stdout),
		LoggerLevel,
	))
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

//func Debug(format string, v ...interface{}) {
//	_logger.Debugf(format, v...)
//}
//
//func Info(format string, v ...interface{}) {
//	_logger.Infof(format, v...)
//}
//
//func Warn(format string, v ...interface{}) {
//	_logger.Warnf(format, v...)
//}
//
//func Error(format string, v ...interface{}) {
//	_logger.Errorf(format, v...)
//}
//
//func Panic(format string, v ...interface{}) {
//	_logger.Panicf(format, v...)
//}
