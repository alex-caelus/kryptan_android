#!/bin/bash
set -e

if [ $# -ne 3 ]
  then
    echo "Usage: \"$0 android-toolchain eabi-name\""
    exit 1
fi

# change these to match your environment
export ANDROID_NDK_ROOT=~/android-ndk/android-ndk-r9d/
export CRYPTOPP_ROOT=~/android-ndk/cryptopp562/
export TARGET_EABI="$3"
export _TOOL_PREFIX="$1"
export _COMPILER="$2"


_CWD=`pwd`

cd "$CRYPTOPP_ROOT"

#set environment variables
. "$_CWD/setenv-android.sh"

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

# continue?
read -p "Everything OK? Press [ENTER] to continue with compilation"

#compile
echo "Compilation started ..."
make clean
make dynamic
echo "Compilation finished!"

#copy compiled lib to destination
echo "Copying libcryptopp.so to correct jni folder"
mkdir "$_CWD/$TARGET_EABI"
cp libcryptopp.so "$_CWD/$TARGET_EABI/libcryptopp.so"

