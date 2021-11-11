/*
 @Author: ingbyr
*/

package exec

import "strings"

type Args struct {
	args  map[string]string
	flags []string
}

func NewArgs() *Args {
	return &Args{
		args:  make(map[string]string),
		flags: make([]string, 0),
	}
}

func (c *Args) Add(flag string) {
	c.flags = append(c.flags, flag)
}

func (c *Args) AddV(flag string, value string) {
	c.Add(flag)
	c.args[flag] = value
}

func (c *Args) Args() []string {
	return strings.Split(c.String(), " ")
}

func (c *Args) String() string {
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
