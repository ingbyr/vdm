/*
 @Author: ingbyr
*/

package uuid

import "github.com/bwmarrin/snowflake"

var Instance, _ = snowflake.NewNode(1)
