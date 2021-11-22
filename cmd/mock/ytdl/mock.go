/*
 @Author: ingbyr
*/

package main

import (
	"bufio"
	_ "embed"
	"fmt"
	"strings"
	"time"
)

/*
 generate mock.txt:
 runtime/engine/yt-dlp_macos ${URL} --newline --no-color --output "runtime/%(title)s.%(ext)s" --format 0 > mock.txt

 build:

*/

//go:embed mock.txt
var output string

func main() {
	reader := strings.NewReader(output)
	scanner := bufio.NewScanner(reader)
	for scanner.Scan() {
		time.Sleep(200 * time.Millisecond)
		fmt.Println(scanner.Text())
	}

	if err := scanner.Err(); err != nil {
		panic(err)
	}
}
