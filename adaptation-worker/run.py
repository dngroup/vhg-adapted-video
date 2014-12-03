__author__ = 'nherbaut'

from adaptation import commons

bitrate=20
target_height=20
file_in="/home/nicolas/vian_pristine.mp4"
folder_out="/home/nicolas/output"
height, width = commons.get_video_size(file_in)
dimensions=commons.compute_target_size(height, width,target_height)
file_out=commons.transcode(file_in,folder_out+"/encoding",dimensions,bitrate)
commons.add_playlist_header(folder_out+"/hls")
version_playlist=commons.chunk_hls(file_out,folder_out+"/hls",dimensions)
commons.add_playlist_info(folder_out+"/hls",version_playlist,bitrate)
commons.add_playlist_footer(folder_out+"/hls")
