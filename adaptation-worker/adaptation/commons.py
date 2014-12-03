import subprocess

__author__ = 'nherbaut'
import math
import os

from pymediainfo import MediaInfo


def get_video_size(input_file):
    '''
    use mediainfo to compute the video size
    :param input_file: the video vile
    :return: a tuple with height and width of the video
    '''
    original_height = 0
    original_width = 0

    media_info = MediaInfo.parse(input_file)
    for track in media_info.tracks:
        if track.track_type == 'Video':
            return track.height, track.width

            break

    raise AssertionError("failed to read video info from " + input_file)


def compute_target_size(original_height, original_width, target_height):
    '''

    :param original_height: the height of the video
    :param original_width: the width of the video
    :param target_height: the height we want to video to have after transcoding
    :return:  a tuple with the target height and width of the video
    '''
    return target_height, math.trunc(float(target_height) / original_height * original_width / 2) * 2


def transcode(file_in, folder_out, dimensions, bitrate):
    '''

    :param file_in: the file to transcode
    :param folder_out: the target folder where the transcoded file will be, it will be created
    :param dimensions: tuple representing the target dimensions
    :param bitrate: target bitrate
    :return: the path of the created file
    '''

    dims = str(dimensions[0]) + "x" + str(dimensions[1])
    dimsp = str(dimensions[0]) + ":" + str(dimensions[1])
    file_out = folder_out \
               + "/" + dims + ".mp4"
    if not os.path.exists(folder_out):
        os.makedirs(folder_out)
    subprocess.call(
        "ffmpeg -i " + file_in + " -c:v libx264 -profile:v main -level 3.1 -b:v 100k -vf scale=" + dimsp + " -c:a aac -strict -2 -force_key_frames expr:gte\(t,n_forced*4\) " + file_out,
        shell=True)
    return file_out


def chunk_hls(file_in, folder_out, dimensions, segtime=4):
    '''
    create hls chunks and the playlist, add the playlist
    :param file_in: the file that needs to be chunked
    :param folder_out: the folder in which you store all hls sub-folders
    :param dimensions: a tuple containing the dimension of the video, used for naming the created folder
    :param bitrate: the bitrate of the target file
    :param segtime: duration for segmentation in second
    :return: the created playlist file path and the bitrate
    '''
    dims = str(dimensions[0]) + "x" + str(dimensions[1])
    target_transcoded_folder = folder_out + "/" + dims
    if not os.path.exists(target_transcoded_folder):
        os.makedirs(target_transcoded_folder)

    args = "ffmpeg -i " + file_in + " -map 0 -flags +global_header -vcodec copy -vbsf h264_mp4toannexb -acodec copy -f segment -segment_format mpegts -segment_time " + str(
        segtime) + " -segment_wrap 0 -segment_list " + target_transcoded_folder + "/playlist.m3u8 " + target_transcoded_folder + "/chunks_name%03d.ts"
    subprocess.call(args, shell=True)
    return target_transcoded_folder + "/playlist.m3u8"


def add_playlist_info(main_playlist_folder, version_playlist_file, bitrate):
    main_playlist_file = main_playlist_folder + "/playlist.m3u8"

    with open(main_playlist_file, "a") as f:
        f.write("#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=" + str(
            bitrate * 1000) + ",RESOLUTION=" + version_playlist_file+"\n")


def add_playlist_header(playlist_folder):
    playlist_file = playlist_folder + "/playlist.m3u8"
    if os.path.exists(playlist_folder):
        os.remove(playlist_folder)
    os.makedirs(playlist_folder)

    with open(playlist_file, "a") as f:
        f.write("#EXTM3U\n")


def add_playlist_footer(playlist_folder):
    playlist_file = playlist_folder + "/playlist.m3u8"
    with open(playlist_file, "a") as f:
        f.write("##EXT-X-ENDLIST")
