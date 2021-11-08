/*
 @Author: ingbyr
*/

package engine

var (
	annie = &Annie{
		engine{
			Version:      "not exist",
			Name:         "annie",
			ExecutorPath: "not exist",
			Valid:        true,
			opts:         EmptyOpts(),
		},
	}
)

func init() {
	register(annie)
}

// Annie downloader engine core 'annie'
type Annie struct {
	engine
}
