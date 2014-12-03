__author__ = 'nherbaut'

from adaptation import commons

file_in="/home/nicolas/vian_pristine.mp4"
folder_out="/home/nicolas/output"
height, width = commons.get_video_size(file_in)


commons.add_playlist_header(folder_out+"/hls")
files_out=[]
for bitrate, target_height in [(20,20),(30,30)]:
    dimensions=commons.compute_target_size(height, width,target_height)
    file_out=commons.transcode(file_in,folder_out+"/encoding",dimensions,bitrate)
    files_out.append(file_out)
    version_playlist=commons.chunk_hls(file_out,folder_out+"/hls",dimensions)
    commons.add_playlist_info(folder_out+"/hls",version_playlist,bitrate)

commons.add_playlist_footer(folder_out+"/hls/")
commons.chunk_dash(files_out,folder_out+"/dash/")


