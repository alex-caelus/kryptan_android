#!/bin/bash
set -e

# change these to match your environment

export ANDROID_NDK_ROOT='/cygdrive/c/AndroidSDK/android-ndk-r9d'
export CRYPTOPP_ROOT='/cygdrive/d/Sync/Programmeringsprojekt/C++/Libs/cryptopp562'
export CPATH='/usr/include'

_CWD=`pwd`

cd $CRYPTOPP_ROOT

#set environment variables
. "$_CWD/setenv-android.sh"
export CPATH='/usr/include'
export C_INCLUDE_PATH='/usr/include'
export CPLUS_INCLUDE_PATH='/usr/include'
export OBJC_INCLUDE_PATH='/usr/include'

function cleanup {
    #revert the patches
    echo "Reverting patch to cryptopp source folder ..."
    patch -R -p0 -i "$_CWD/GNUmakefile-android.patch"
    patch -R -p0 -i "$_CWD/config.h.patch"
}
trap cleanup EXIT

# apply patch to cryptopp source
echo "Applying patch to cryptopp source folder ..."
patch -p0 -i "$_CWD/GNUmakefile-android.patch"
patch -p0 -i "$_CWD/config.h.patch"

# continue?
read -p "Everything OK? Press [ENTER] to continue with compilation"

#compile
echo "Compilation started ..."
make dynamic
echo "Compilation finished!"

#copy compiled lib to destination
echo "Copying libcryptopp.so to correct jni folder"