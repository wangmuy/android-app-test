# https://developer.android.com/studio/projects/add-native-code.html?hl=zh-cn

# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.
cmake_minimum_required(VERSION 3.6.0)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.
add_library(NativeUtils # library name
    SHARED # SHARED/STATIC
    com_example_test_NativeUtils.cpp # relative path
)

# Specifies a path to native header files.
include_directories(
    .
)

find_library(# Defines the name of the path variable that stores the
    # location of the NDK library.
    log-lib

    # Specifies the name of the NDK library that
    # CMake needs to locate.
    log
)

# Links your native library against one or more other native libraries.
target_link_libraries( # Specifies the target library.
    NativeUtils

    # Links the log library to the target library.
    ${log-lib}
)

add_custom_target(
        rustlibCompile
        COMMAND cd ${CMAKE_CURRENT_SOURCE_DIR}/rust && cargo build --release
        COMMAND ${CMAKE_COMMAND} -E copy ${CMAKE_CURRENT_SOURCE_DIR}/rust/target/aarch64-linux-android/release/librustlib.so ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/arm64-v8a/
)
add_dependencies(NativeUtils rustlibCompile)
