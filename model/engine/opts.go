/*
 @Author: ingbyr
*/

package engine

import "strings"

type opts struct {
	args  map[string]string
	flags []string
}

func EmptyOpts() *opts {
	return &opts{
		args:  make(map[string]string),
		flags: make([]string, 0),
	}
}

func (c *opts) addCmdFlag(flag string) {
	c.flags = append(c.flags, flag)
}

func (c *opts) addCmdFlagValue(flag string, value string) {
	c.addCmdFlag(flag)
	c.args[flag] = value
}

func (c *opts) toCmdStrSlice() []string {
	return strings.Split(c.toCmdStr(), " ")
}

func (c *opts) toCmdStr() string {
	sp := " "
	var sb strings.Builder
	for _, f := range c.flags {
		if sb.Len() != 0 {
			sb.WriteString(sp)
		}
		sb.WriteString(f)
		if v, ok := c.args[f]; ok {
			sb.WriteString(sp)
			sb.WriteString(v)
		}
	}
	return sb.String()
}
