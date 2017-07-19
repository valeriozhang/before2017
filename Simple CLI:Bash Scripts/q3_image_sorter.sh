#!/bin/bash
cd $1

ls -Rrtd $PWD/**/* | grep ".jpg" > mergedphotos.txt
while read line; do
echo "$line"
done < mergedphotos2.txt
paste -s mergedphotos2.txt | convert -append mergedphotos2.txt merged_seasons.jpg

while [-z "$1"]
do
echo "nothing was entered""
done


