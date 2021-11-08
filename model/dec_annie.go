/*
 @Author: ingbyr
*/

package model

var (
	downloaderAnnie = &DecAnnie{
		decBase: &decBase{
			DecInfo: &DecInfo{
				Version:      "not exist",
				Name:         "annie",
				ExecutorPath: "not exist",
			},
			CmdArgs: NewCmdArgs(),
			Valid:   true,
		},
	}
)

func init() {
	DecManager.Register(downloaderAnnie)
}

// DecAnnie downloader engine core 'annie'
type DecAnnie struct {
	*decBase
}
