/*
 @Author: ingbyr
*/

package annie

import "github.com/ingbyr/vdm/app/engine"

var (
	annie = &Annie{
		Config: engine.Config{
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

// Annie downloader Config core 'annie'
type Annie struct {
	engine.Config
}
