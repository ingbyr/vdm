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

var atom zap.AtomicLevel

var base *zap.Logger
var sugared *zap.SugaredLogger

func init() {
	atom = zap.NewAtomicLevel()
	encoderCfg := zap.NewProductionEncoderConfig()
	base = zap.New(
		zapcore.NewCore(
			zapcore.NewJSONEncoder(encoderCfg),
			zapcore.Lock(os.Stdout),
			atom),
		zap.AddCaller(),
	)
	defer base.Sync()
	sugared = base.Sugar()
}

func SetLevel(level string) {
	var newLevel zapcore.Level
	err := newLevel.Set(level)
	if err != nil {
		panic("unavailable log atom: " + level)
	}
	atom.SetLevel(newLevel)
}

func Gin() *zap.Logger {
	return base.Named("gin")
}

func Gorm() zapgorm2.Logger {
	return zapgorm2.New(base.Named("db"))
}

func New(name string) *zap.SugaredLogger {
	return sugared.Named(name)
}
