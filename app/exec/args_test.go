/*
 @Author: ingbyr
*/

package exec

import (
	"fmt"
	"testing"
)

func TestCmdArgs(t *testing.T) {
	cmdArgs := NewArgs()
	cmdArgs.Add("-j")
	cmdArgs.Add("https://demo-url")
	cmdArgs.AddV("--proxy", "socks5://$address:$port")
	fmt.Println(cmdArgs.String())
}
