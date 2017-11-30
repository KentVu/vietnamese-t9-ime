LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := dawgdic-build

LOCAL_SRC_FILES := src/main/cpp/dawgdic-build.cc

LOCAL_CPP_EXTENSION := .cc

include $(BUILD_SHARED_LIBRARY)