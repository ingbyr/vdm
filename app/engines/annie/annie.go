/*
 @Author: ingbyr
*/

package annie

import "github.com/ingbyr/vdm/app/engine"

var (
	annie = &Annie{
		Base: engine.Base{
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

// Annie downloader Base core 'annie'
type Annie struct {
	engine.Base
}
