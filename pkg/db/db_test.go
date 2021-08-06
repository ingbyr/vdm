/*
 @Author: ingbyr
*/

package db

import (
	_ "github.com/mattn/go-sqlite3"
	"testing"
)

func TestDb_Init(t *testing.T) {
	Setup("tmp.db")
	InitSchemes()
}
