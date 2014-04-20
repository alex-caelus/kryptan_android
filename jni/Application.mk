APP_ABI := all
APP_CPPFLAGS += -fexceptions -frtti -std=c++11 -DDEBUG=0
#APP_STL := stlport_shared
APP_STL := gnustl_shared
APP_PLATFORM := android-9
NDK_DEBUG=0
APP_USE_CPP0X := true
NDK_TOOLCHAIN_VERSION := 4.8