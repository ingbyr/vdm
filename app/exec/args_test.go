/*
 @Author: ingbyr
*/

package exec

import (
	"fmt"
	"testing"
)

func TestCmdArgs(t *testing.T) {
	cmdArgs := NewArgs("test")
	cmdArgs.Add("-j")
	cmdArgs.AddV("--resume", "true")
	cmdArgs.Add("https://demo-url")
	cmdArgs.AddV("--proxy", "socks5://$address:$port")
	fmt.Println(cmdArgs.String())
}
