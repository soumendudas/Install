LOCAL_PATH := $(call my-dir)

   include $(CLEAR_VARS)

   LOCAL_MODULE    := checkinstall
   LOCAL_SRC_FILES := checkinstall.c
   
   LOCAL_LDLIBS := -llog

   include $(BUILD_SHARED_LIBRARY)