#!/bin/bash

set -e

NOPROMPT=$1

#for target in "arm-linux-androideabi 4.8 armeabi 1" "arm-linux-androideabi 4.8 armeabi-v7a 1" "i686-linux-android 4.8 x86 0" "mipsel-linux-android 4.8 mips 1"
for target in "arm-linux-androideabi-4.8 armeabi arm-linux-androideabi 1" "arm-linux-androideabi-4.8 armeabi-v7a arm-linux-androideabi 1" "x86-4.8 x86 i686-linux-android 1" "mipsel-linux-android-4.8 mips mipsel-linux-android 1"
do
	set -- $target

	REPLY="n"
	if [ x$NOPROMPT != xy ]; then
		read -p "Are you want to attempt compiling for $2? " -n 1 -r
		echo    # (optional) move to a new line
	else
		REPLY="y"
	fi
	if [[ $REPLY =~ ^[Yy]$ ]]
	then
	        ./compile-one-target.sh $1 $2 $3 $4
	fi
done

exit 0
