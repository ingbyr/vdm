/*
 @Author: ingbyr
*/

package engine

var (
	annie = &Annie{
		engine{
			config: config{
				Version:      "not exist",
				Name:         "annie",
				ExecutorPath: "not exist",
				Valid:        true,
			},
			opts: nil,
		},
	}
)

func init() {
	register(annie)
}

// Annie downloader config core 'annie'
type Annie struct {
	engine
}
