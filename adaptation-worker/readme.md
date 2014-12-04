= Prerequisites =

== Native Packages ==
* ffmpeg (ubuntu / sudo add-apt-repository ppa:jon-severinsson/ffmpeg)
* mediainfo (ubuntu/ sudo apt-get install mediainfo)
* lixml2 (sudo apt-get install libxml2-dev libxslt-dev)

== python packages==

* pymediainfo
* lxml

== example ==
download_file.apply_async(("http://download.wavetlan.com/SVV/Media/HTTP/H264/Talkinghead_Media/H264_test3_Talkingheadclipped_mp4_480x360.mp4","12345-w"))
download.apply_async(("http://google.com","123"),link=chord(group([transcode.s(400,5),transcode.s(400,1),transcode.s(400,3), transcode.s(500,10)]),group(hls.s(),dash.s())))


