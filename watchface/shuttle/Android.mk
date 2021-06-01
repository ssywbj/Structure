LOCAL_PATH := $(call my-dir)
ifeq ($(strip $(APK_WIZFACE_SHUTTLE_SUPPORT)),yes)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := PRESIGNED
LOCAL_MODULE := WizFace_Shuttle
LOCAL_SRC_FILES := $(LOCAL_MODULE).apk

LOCAL_MODULE_PATH :=  $(TARGET_OUT)/etc/wiz_home/plugin_watch
LOCAL_DEX_PREOPT := false

include $(BUILD_PREBUILT)
endif