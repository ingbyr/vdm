/*
 @Author: ingbyr
*/

package model

import (
	"fmt"
	"testing"
)

func TestCmdArgs(t *testing.T) {
	cmdArgs := NewCmdArgs()
	cmdArgs.addCmdFlag("-j")
	cmdArgs.addCmdFlag("https://demo-url")
	cmdArgs.addCmdFlagValue("--proxy", "socks5://$address:$port")
	fmt.Println(cmdArgs.toCmdStr())
}
