LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPPFLAGS = -std=c++0x
LOCAL_MODULE    := kryptan_core
CORE_FILE_LIST :=  $(wildcard $(LOCAL_PATH)/kryptan_core/*.cpp)
THIS_FILE_LIST :=  $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES := $(THIS_FILE_LIST:$(LOCAL_PATH)/%=%) $(CORE_FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_SHARED_LIBRARIES := cryptopp

include $(BUILD_SHARED_LIBRARY)