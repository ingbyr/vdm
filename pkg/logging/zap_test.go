/*
 @Author: ingbyr
*/

package logging

import (
	"go.uber.org/zap/zapcore"
	"testing"
)

func TestZap(t *testing.T) {
	LoggerLevel.SetLevel(zapcore.DebugLevel)
	Debug("debug msg %v", 123)
	LoggerLevel.SetLevel(zapcore.InfoLevel)
	Debug("debug msg with info level")
}
