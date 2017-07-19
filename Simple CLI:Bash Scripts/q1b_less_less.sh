#!/bin/bash
var1="$(ps -ef | grep -w "less" | grep -v grep | awk '{print $2}')"
kill -9 $var1
