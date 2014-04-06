#!/bin/bash

set -e

for target in "arm-linux-androideabi 4.8 armeabi" "arm-linux-androideabi 4.8 armeabi-v7a" "x86 4.8 x86" "mipsel-linux-android 4.8 mips"
do
	set -- $target
	./compile-one-target.sh $1 $2 $3
done

exit 0
