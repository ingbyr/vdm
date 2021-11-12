/*
 @Author: ingbyr
*/

package task

import "context"

type MTask struct {
	Engine   string             `form:"engine"`
	MediaUrl string             `form:"mediaUrl"`
	Ctx      context.Context    `form:"-"`
	Cancel   context.CancelFunc `form:"-"`
}
