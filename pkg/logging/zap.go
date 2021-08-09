/*
 @Author: ingbyr
*/

package logging

import (
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"gorm.io/gorm/logger"
	"moul.io/zapgorm2"
	"os"
)

var _logger *zap.SugaredLogger
var GinLogger *zap.Logger
var DBLogger zapgorm2.Logger
var LoggerLevel zap.AtomicLevel

func init() {
	LoggerLevel = zap.NewAtomicLevel()
	config := zap.NewProductionEncoderConfig()
	GinLogger = zap.New(zapcore.NewCore(
		zapcore.NewJSONEncoder(config),
		zapcore.Lock(os.Stdout),
		LoggerLevel,
	))
	LoggerLevel.SetLevel(zapcore.DebugLevel)
	_logger = GinLogger.Sugar()
	DBLogger = zapgorm2.New(GinLogger)
	DBLogger.LogLevel = logger.Info
}

func Debug(format string, v ...interface{}) {
	_logger.Debugf(format, v...)
}

func Info(format string, v ...interface{}) {
	_logger.Infof(format, v...)
}

func Warn(format string, v ...interface{}) {
	_logger.Warnf(format, v...)
}

func Error(format string, v ...interface{}) {
	_logger.Errorf(format, v...)
}

func Panic(format string, v ...interface{}) {
	_logger.Panicf(format, v...)
}
