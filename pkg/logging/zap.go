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
	logLevel := zap.NewAtomicLevel()
	// TODO set from config file
	logLevel.SetLevel(zap.DebugLevel)
	encoderCfg := zap.NewProductionEncoderConfig()
	baseLog = zap.New(
		zapcore.NewCore(
			zapcore.NewJSONEncoder(encoderCfg),
			zapcore.Lock(os.Stdout),
			logLevel),
		zap.AddCaller(),
	)
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
