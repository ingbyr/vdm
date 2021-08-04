/*
 @Author: ingbyr
*/

package downloader

import (
	"fmt"
	"testing"
)

func TestCmdArgs(t *testing.T) {
	cmdArgs := NewCmdArgs()
	cmdArgs.addFlag("-j")
	cmdArgs.addFlag("https://demo-url")
	cmdArgs.addFlagValue("--proxy", "socks5://$address:$port")
	fmt.Println(cmdArgs.toCmdStr())
}
