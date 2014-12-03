#!/bin/bash
#Le $1 correspond au fichier entrant dans le transcodeur
#Le $2 correspond au fichier de sortie (uniquement le nom sans l'extension qui est mp4 de toute manière)
#$3 is the target bitrate
#$4 is the target HEIGHT

echo $1
WIDTH=$( mediainfo $1 | grep Width | cut -d ':' -f 2 | sed 's/[^0-9]//g' )
HEIGHT=$( mediainfo $1 | grep Height | cut -d ':' -f 2 | sed 's/[^0-9]//g' )
echo "Width : $WIDTH"
echo "Height : $HEIGHT"
BITRATE_TARGET=$3
# Ratio
HEIGHT_TARGET=$4

WIDTH_TARGET=$(($HEIGHT_TARGET*$WIDTH/$HEIGHT))
if [[ $(($WIDTH_TARGET % 2)) == 1 ]]; then
WIDTH_TARGET=$(($WIDTH_TARGET+1))
fi
echo "Width target: $WIDTH_TARGET"
echo "Height target: $HEIGHT_TARGET"
TARGET_FILE=$2/encoding/"$WIDTH_TARGET"x"$HEIGHT_TARGET"_"$BITRATE_TARGET".mp4
echo "Target File:  $TARGET_FILE"
#Encoding
mkdir -p $2/encoding
ffmpeg -i $1 -c:v libx264 -profile:v main -level 3.1 -b:v ${BITRATE_TARGET}k -vf scale=$WIDTH_TARGET:$HEIGHT_TARGET -c:a aac -strict -2 -force_key_frames expr:gte\(t,n_forced*4\) $TARGET_FILE
echo $TARGET_FILE

