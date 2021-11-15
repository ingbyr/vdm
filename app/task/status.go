/*
 @Author: ingbyr
*/

package task

type status = int

const (
	// Created avoid default zero
	Created status = iota + 1
	Downloading
	Merging
	Paused
	Completed
	Failed
)
