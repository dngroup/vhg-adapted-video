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
TARGET_FILE=$2/encoding/"$WIDTH_TARGET"x"$HEIGHT_TARGET.mp4"
echo "Target File:  $TARGET_FILE"
#Encoding
mkdir -p $2/encoding
ffmpeg -i $1 -c:v libx264 -profile:v main -level 3.1 -b:v ${BITRATE_TARGET}k -vf scale=$WIDTH_TARGET:$HEIGHT_TARGET -c:a aac -strict -2 -force_key_frames expr:gte\(t,n_forced*4\) $TARGET_FILE
# Chunks HLS
mkdir -p $2/hls
HLS_FOLDER_TARGET=$2/hls/hls-"$WIDTH_TARGET"x"$HEIGHT_TARGET.mp4"
mkdir $HLS_FOLDER_TARGET
ffmpeg -i $TARGET_FILE -map 0 -flags +global_header -vcodec copy -vbsf h264_mp4toannexb -acodec copy -f segment -segment_format mpegts -segment_time 4 -segment_wrap 0 -segment_list $HLS_FOLDER_TARGET/playlist.m3u8 $HLS_FOLDER_TARGET/chunks_name%03d.ts

#if playlist doesn't exist yet, create it with the HEAD
if [ ! -f $2/hls/playlist.m3u8 ]; then
	touch $2/hls/playlist.m3u8
	echo "#EXTM3U
#EXT-X-ENDLIST" > $2/hls/playlist.m3u8
fi

#remove the last line
head -n -1 $2/hls/playlist.m3u8 > $2/hls/playlist.m3u8_temp
rm $2/hls/playlist.m3u8
mv $2/hls/playlist.m3u8_temp $2/hls/playlist.m3u8

echo "#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=$(($BITRATE_TARGET*1000)),RESOLUTION=$HLS_FOLDER_TARGET/playlist.m3u8
#EXT-X-ENDLIST"  >> $2/hls/playlist.m3u8



# Chunks Dash
mkdir $2/dash
TARGET_FILE=$2/encoding/"$WIDTH_TARGET"x"$HEIGHT_TARGET.mp4"
MP4Box -dash 4000 -profile onDemand $2/encoding/"$WIDTH_TARGET"x"$HEIGHT_TARGET.mp4"#video:id=v1 -out $2/dash/"$WIDTH_TARGET"x"$HEIGHT_TARGET.mp4"
rm -rf $TARGET_FILE
