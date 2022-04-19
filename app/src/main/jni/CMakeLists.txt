# https://developer.android.com/studio/projects/add-native-code.html?hl=zh-cn

# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.
cmake_minimum_required(VERSION 3.6.0)

set(CARGO_LIB_NAME rustrocks)

if(${ANDROID_ABI} STREQUAL arm64-v8a)
    set(CARGO_ABI aarch64-linux-android)
elseif(${ANDROID_ABI} STREQUAL armeabi-v7a)
    set(CARGO_ABI armv7-linux-androideabi)
endif()

set(CARGO_BUILD_TYPE release)
set(CARGO_FLAGS ${CARGO_FLAGS} --target ${CARGO_ABI})
if(NOT DEFINED CARGO_BUILD_TYPE)
    string(TOUPPER ${CMAKE_BUILD_TYPE} CMAKE_BUILD_TYPE_UPPER)
    if(CMAKE_BUILD_TYPE_UPPER STREQUAL DEBUG)
        set(CARGO_BUILD_TYPE debug)
    else()
        set(CARGO_BUILD_TYPE release)
    endif()
endif()
if(CARGO_BUILD_TYPE STREQUAL release)
    set(CARGO_BUILD_TYPE_FLAG --${CARGO_BUILD_TYPE})
endif()

add_custom_target(
        ${CARGO_LIB_NAME}
        COMMAND cd ${CMAKE_CURRENT_SOURCE_DIR}/rust && cargo ndk ${CARGO_FLAGS} build ${CARGO_BUILD_TYPE_FLAG}
        COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}
        COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/rust/target/${CARGO_ABI}/${CARGO_BUILD_TYPE}/lib${CARGO_LIB_NAME}.so ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/
)
