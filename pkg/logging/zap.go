/*
 @Author: ingbyr
*/

package logging

import (
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"os"
)

var logger *zap.SugaredLogger
var GinLogger *zap.Logger
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
	logger = GinLogger.Sugar()
}

func Debug(format string, v ...interface{}) {
	logger.Debugf(format, v...)
}

func Info(format string, v ...interface{}) {
	logger.Infof(format, v...)
}

func Warn(format string, v ...interface{}) {
	logger.Warnf(format, v...)
}

func Error(format string, v ...interface{}) {
	logger.Errorf(format, v...)
}

func Panic(format string, v ...interface{}) {
	logger.Panicf(format, v...)
}
