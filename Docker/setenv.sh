#! /bin/sh
# ======================================
#  ______                           __
# /_  __/___  ____ ___  _________ _/ /_
#  / / / __ \/ __ `__ \/ ___/ __ `/ __/
# / / / /_/ / / / / / / /__/ /_/ / /_
#/_/  \____/_/ /_/ /_/\___/\__,_/\__/
#
# ======================================

export JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"

#### export CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/./urandom"

echo "_______________________________________________"
echo ""
echo "Using CATALINA_OPTS:"
for arg in $CATALINA_OPTS
do
    echo ">> " $arg
done
echo ""
 
echo "Using JAVA_OPTS:"
for arg in $JAVA_OPTS
do
    echo ">> " $arg
done
echo "_______________________________________________"
echo ""

