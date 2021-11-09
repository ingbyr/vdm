/*
 @Author: ingbyr
*/

package engine

//type config interface {
//	Name() string
//	Version() string
//	ExecutorPath() string
//	Valid() bool
//	SetValid(Valid bool)
//}

type config struct {
	Version      string `json:"version"`
	Name         string `json:"name"`
	ExecutorPath string `json:"executorPath"`
	Valid        bool   `json:"valid"`
}

//func (c *config) Name() string {
//	return c.Name
//}
//
//func (c *config) Version() string {
//	return c.Version
//}
//
//func (c *config) ExecutorPath() string {
//	return c.ExecutorPath
//}
//
//func (c *config) Valid() bool {
//	return c.Valid
//}
//
//func (c *config) SetValid(Valid bool) {
//	c.Valid = Valid
//}
