export HADOOP_HEAPSIZE=8000
export HADOOP_USER_CLASSPATH_FIRST=true
JARS=/home/trbatcha/javacpp-presets/target/opencv-linux-x86_64.jar:/home/trbatcha/javacpp-presets/target/javacpp.jar:/home/trbatcha/javacpp-presets/target/opencv.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-jpeg/target/twelvemonkeys-imageio-jpeg-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-pnm/target/twelvemonkeys-imageio-pnm-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-tiff/target/twelvemonkeys-imageio-tiff-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-core/target/twelvemonkeys-imageio-core-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/common/common-lang/target/twelvemonkeys-common-lang-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/common/common-io/target/twelvemonkeys-common-io-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-metadata/target/twelvemonkeys-imageio-metadata-3.3-SNAPSHOT.jar

export HADOOP_CLASSPATH=$JARS
hadoop jar $HOME/hipi-release/tools/hibImport/build/libs/hibImport.jar -f $*

