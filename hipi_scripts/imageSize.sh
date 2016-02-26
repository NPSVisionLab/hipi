
hadoop jar $HOME/hipi-release/tools/imageSize/build/libs/imageSize.jar -conf imageSize.xml $1 outputSize
if [ "$?" != "0" ]; then
    echo "hadoop failed"
    exit 1
fi
echo "Merging and copying output to local drive"
hadoop fs -getmerge outputSize outputSize.txt

