/*
 @Author: ingbyr
*/

package annie

import "github.com/ingbyr/vdm/app/engine"

var (
	annie = &Annie{
		Info: engine.Info{
			Version:  "not exist",
			Name:     "annie",
			Executor: "not exist",
			Enable:   false,
			Valid:    false,
		},
	}
)

func init() {
	engine.Register(annie)
}

// Annie downloader Info core 'annie'
type Annie struct {
	engine.Info
}
