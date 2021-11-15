/*
 @Author: ingbyr
*/

package task

import "context"

type MTask struct {
	Engine   string             `form:"engine" binding:"required"`
	MediaUrl string             `form:"mediaUrl" binding:"required"`
	Ctx      context.Context    `form:"-"`
	Cancel   context.CancelFunc `form:"-"`
}
