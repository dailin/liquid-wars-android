include $(CLEAR_VARS)

MY_LOCAL_PATH := $(LOCAL_PATH)/dotsimulation

LOCAL_MODULE := dotsimulation

MY_LOCAL_SRC_FILES := $(wildcard $(MY_LOCAL_PATH)/*.cpp)
LOCAL_SRC_FILES := $(subst jni/, , $(MY_LOCAL_SRC_FILES))

LOCAL_EXPORT_C_INCLUDES := $(MY_LOCAL_PATH)

LOCAL_CFLAGS := -Wall -Werror

include $(BUILD_STATIC_LIBRARY)
