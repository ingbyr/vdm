/*
 @Author: ingbyr
*/

package logging

import (
	"go.uber.org/zap/zapcore"
	"testing"
)

func TestZap(t *testing.T) {
	atom.SetLevel(zapcore.DebugLevel)
	Debug("debug msg %v", 123)
	atom.SetLevel(zapcore.InfoLevel)
	Debug("debug msg with info atom")
}
