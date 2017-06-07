# https://developer.android.com/studio/projects/add-native-code.html?hl=zh-cn

cmake_minimum_required(VERSION 3.6.0)

#set (CMAKE_CXX_STANDARD 11)

add_library(NativeUtils
    SHARED
    com_example_test_NativeUtils.cpp
)

# Specifies a path to native header files.
include_directories(.)

# nodejs so
add_library(node
    SHARED
    IMPORTED)

set_target_properties( node
    PROPERTIES
    IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/prebuilt/lib/${ANDROID_ABI}/libnode.so
)

# https://nodejs.org/download/release/v7.7.3/node-v7.7.3-headers.tar.gz
include_directories(prebuilt/include/node-v7.7.3)

find_library( log-lib log )
target_link_libraries( NativeUtils ${log-lib} node )
