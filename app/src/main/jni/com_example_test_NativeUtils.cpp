#include <cstdio>
#include <thread>
#include <android/log.h>
#include <unistd.h>
#include <vector>
#include "prebuilt/include/node-v7.7.3/node.h"
#include "com_example_test_NativeUtils.h"

#define LOGD(_LOG_TAG, ...)  __android_log_print(ANDROID_LOG_DEBUG, _LOG_TAG, __VA_ARGS__)
#define LOGI(_LOG_TAG, ...)  __android_log_print(ANDROID_LOG_INFO, _LOG_TAG, __VA_ARGS__)
#define LOGW(_LOG_TAG, ...)  __android_log_print(ANDROID_LOG_WARN, _LOG_TAG, __VA_ARGS__)
#define LOGE(_LOG_TAG, ...)  __android_log_print(ANDROID_LOG_ERROR, _LOG_TAG, __VA_ARGS__)
#define LOGF(_LOG_TAG, ...)  __android_log_print(ANDROID_LOG_FATAL, _LOG_TAG, __VA_ARGS__)

#define LOG_TAG "NativeUtils"
#define LOG_NODEJS "nodejs"

static void redirectOutput();

JNIEXPORT jstring JNICALL Java_com_example_test_NativeUtils_getCString(JNIEnv* env, jclass obj)
{
    //return (*env)->NewStringUTF(env, "hello c-style from jni");
    return env->NewStringUTF("hello cpp-style from jni");
}

JNIEXPORT jint JNICALL Java_com_example_test_NativeUtils_startNode(
        JNIEnv* env, jclass obj, jobjectArray args)
{
    jint ret = (jint)0;

    int count = env->GetArrayLength(args);

    std::vector<char> buffer;
    for(int i=0; i < count; ++i) {
        jstring str = (jstring)env->GetObjectArrayElement(args, i);
        const char* sptr = env->GetStringUTFChars(str, 0);

        do {
            buffer.push_back(*sptr);
        } while(*sptr++ != '\0');
    }

    std::vector<char*> argv;
    argv.push_back(&buffer[0]);
    for(int i=0; i < buffer.size()-1; ++i) {
        if(buffer[i] == '\0') {
            argv.push_back(&buffer[i + 1]);
        }
    }
    argv.push_back(NULL);

    ret = node::Start(argv.size()-1, argv.data());
    LOGD(LOG_TAG, "node ret=%d", ret);

    return ret;
}

static void redirectOutput() {
    setvbuf(stdout, 0, _IOLBF, 0);
    setvbuf(stderr, 0, _IONBF, 0);
    static int pfd[2];
    pipe(pfd);
    dup2(pfd[1], 1);
    dup2(pfd[1], 2);

    auto logger = std::thread([](int* pipefd) {
        char buf[128];
        ssize_t nBytes = 0;
        LOGD(LOG_TAG, "new thread here!");
        while((nBytes=read(pfd[0], buf, sizeof(buf)-1)) > 0) {
            if(buf[nBytes-1] == '\n')
                --nBytes;
            buf[nBytes] = '\0';
            LOGD(LOG_NODEJS, "%s", buf);
        }
        LOGD(LOG_TAG, "logger thread end!");
    }, pfd);
    logger.detach();
}

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;

    LOGD(LOG_TAG, "JNI_OnLoad");

    if(vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        goto bail;
    }
    env = uenv.env;

    redirectOutput();

    result = JNI_VERSION_1_4;

bail:
    return result;
}
