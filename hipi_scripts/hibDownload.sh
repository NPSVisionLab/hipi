hadoop fs -mkdir download
hadoop fs -copyFromLocal ~/hipi-release/testdata/downloader-images.txt download/images.txt
hadoop fs -cat download/images.txt
~/hipi-release/tools/hibDownload.sh download/images.txt download.hib --num-nodes 10
~/hipi-release/tools/hibInfo.sh  download.hib --show-meta
