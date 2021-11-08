/*
 @Author: ingbyr
*/

package engine

import (
	"fmt"
	"testing"
)

func TestCmdArgs(t *testing.T) {
	cmdArgs := EmptyOpts()
	cmdArgs.addCmdFlag("-j")
	cmdArgs.addCmdFlag("https://demo-url")
	cmdArgs.addCmdFlagValue("--proxy", "socks5://$address:$port")
	fmt.Println(cmdArgs.toCmdStr())
}
