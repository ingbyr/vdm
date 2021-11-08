/*
 @Author: ingbyr
*/

package engine

var (
	downloaderAnnie = &DecAnnie{
		decBase: &engine{

			Version:      "not exist",
			Name:         "annie",
			ExecutorPath: "not exist",

			opts:  NewCmdArgs(),
			Valid: true,
		},
	}
)

func init() {
	register(downloaderAnnie)
}

// DecAnnie downloader engine core 'annie'
type DecAnnie struct {
	*decBase
}
