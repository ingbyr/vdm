/*
 @Author: ingbyr
*/

package model

import "strings"

type CmdArgs struct {
	args  map[string]string
	flags []string
}

func NewCmdArgs() CmdArgs {
	return CmdArgs{
		args:  make(map[string]string),
		flags: make([]string, 0),
	}
}

func (c *CmdArgs) addFlag(flag string) {
	c.flags = append(c.flags, flag)
}

func (c *CmdArgs) addFlagValue(flag string, value string) {
	c.addFlag(flag)
	c.args[flag] = value
}

func (c *CmdArgs) toCmdStrSlice() []string {
	return strings.Split(c.toCmdStr(), " ")
}

func (c *CmdArgs) toCmdStr() string {
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
