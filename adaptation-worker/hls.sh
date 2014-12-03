#!/bin/bash
#$1 in transcoded file path
#Le $2 correspond au fichier de sortie (uniquement le nom sans l'extension qui est mp4 de toute manière)
#$3 is the target bitrate
#$4 is the target HEIGHT


# Chunks HLS

TARGET_FILE=$1
HLS_FOLDER_TARGET=`dirname $file $1`/../hls/
mkdir -p $2/hls
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
