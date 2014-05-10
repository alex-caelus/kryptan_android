LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_CPPFLAGS = -DKRYPTAN_CORE_DO_NOT_USE_SERVER
LOCAL_MODULE    := kryptan_core
LOCAL_LDLIBS    := -llog
NDK_DEBUG=1
CORE_FILE_LIST :=  $(wildcard $(LOCAL_PATH)/kryptan_core/*.cpp) $(wildcard $(LOCAL_PATH)/kryptan_core/SecureString/*.cpp)
THIS_FILE_LIST :=  $(wildcard $(LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES := $(THIS_FILE_LIST:$(LOCAL_PATH)/%=%) $(CORE_FILE_LIST:$(LOCAL_PATH)/%=%)
LOCAL_SHARED_LIBRARIES := cryptopp

include $(BUILD_SHARED_LIBRARY)