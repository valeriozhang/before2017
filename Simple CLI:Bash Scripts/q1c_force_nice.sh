#!/bin/bash
while true
do
var2="$(ps al | grep -w "sleep" | grep -v grep | awk '{print $6}')"
if [[ $var2 -le 4 ]]; then
	var3="$(ps al | grep -w "sleep" | grep -v grep | awk '{print $2}')"
	kill -9 $var3
fi
done
