export HADOOP_HEAPSIZE=4000
export HADOOP_USER_CLASSPATH_FIRST=true
JARS=/home/trbatcha/javacpp-presets/target/opencv-linux-x86_64.jar:/home/trbatcha/javacpp-presets/target/javacpp.jar:/home/trbatcha/javacpp-presets/target/opencv.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-jpeg/target/twelvemonkeys-imageio-jpeg-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-pnm/target/twelvemonkeys-imageio-pnm-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-tiff/target/twelvemonkeys-imageio-tiff-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-core/target/twelvemonkeys-imageio-core-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/common/common-lang/target/twelvemonkeys-common-lang-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/common/common-io/target/twelvemonkeys-common-io-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/imageio/imageio-metadata/target/twelvemonkeys-imageio-metadata-3.3-SNAPSHOT.jar:/home/trbatcha/TwelveMonkeys/common/common-image/target/twelvemonkeys-common-image-3.3-SNAPSHOT.jar
#LIBJARS=`echo ${JARS} | sed s/:/,/g`
export HADOOP_CLASSPATH=${JARS}:${HADOOP_CLASSPATH}
export CLASSPATH=$HADOOP_CLASSPATH:$CLASSPATH
#export YARN_OPTS="-Xmx5025955249 -verbose:class -Djava.net.preferIPv4Stack=true $YARN_OPTS"
export YARN_OPTS="-Xmx5025955249  -Djava.net.preferIPv4Stack=true"
#export HADOOP_CLIENT_OPTS="-Xmx5025955249 -Djava.net.preferIPv4Stack=true"
# HADOOP_CONF_DIR override is required to override the default yarn
# values defined (namely 2 GB max virtual memory and 800k yarn java heap)
# Setting this also gives more debug information.
export HADOOP_CONF_DIR=$HOME/hipi_scripts/covar.xml
#export YARN_LOG_DIR=$HOME/mylogs
hadoop jar $HOME/hipi-release/tools/covar/build/libs/covar.jar  test.hib output



