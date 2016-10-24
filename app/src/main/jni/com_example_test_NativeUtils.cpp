#include <jni.h>
#include "android/log.h"
#include "com_example_test_NativeUtils.h"

JNIEXPORT jstring JNICALL Java_com_example_test_NativeUtils_getCString(JNIEnv* env, jclass obj)
{
    //return (*env)->NewStringUTF(env, "hello c-style from jni");
    return env->NewStringUTF("hello cpp-style from jni");
}