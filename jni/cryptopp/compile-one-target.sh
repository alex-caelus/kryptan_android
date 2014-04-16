#!/bin/bash
set -e

if [ $# -ne 4 ]
  then
    echo "Usage: \"$0 android-toolchain eabi-name tool-prefix <patch-src (1/0)>\""
    exit 1
fi

# change these to match your environment
export ANDROID_NDK_ROOT=~/android-ndk/android-ndk-r9d
export CRYPTOPP_ROOT=~/android-ndk/cryptopp562
export TARGET_EABI="$2"
export _ANDROID_EABI="$1"
export _TOOL_PREFIX="$3"
DO_PATCH=$4

_CWD=`pwd`

cd "$CRYPTOPP_ROOT"

#set environment variables
. "$_CWD/setenv-android.sh"

if [ $DO_PATCH -eq 1 ]
then
	function cleanup {
	    #revert the patches
	    echo "Reverting patch to cryptopp source folder ..."
	    patch -R -p0 -i "$_CWD/GNUmakefile-$TARGET_EABI-android.patch"
	    patch -R -p0 -i "$_CWD/config.h.patch"
	}
	trap cleanup EXIT

	# apply patch to cryptopp source
	echo "Applying patch to cryptopp source folder ..."
	patch -p0 -i "$_CWD/GNUmakefile-$TARGET_EABI-android.patch"
	patch -p0 -i "$_CWD/config.h.patch"
fi

#Store definesa
$CXX -dM -E - < /dev/null > "$_CWD/$TARGET_EABI/defines.txt"

#compile
echo "Compilation started ..."
make clean
make -j 9 dynamic
echo "Compilation finished!"

#copy compiled lib to destination
echo "Copying libcryptopp.so to correct jni folder"
cp libcryptopp.so "$_CWD/$TARGET_EABI/libcryptopp.so"

