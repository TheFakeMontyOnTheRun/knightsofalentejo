cmake_minimum_required(VERSION 3.4.1)
project(NdkGlue)
# now build app's shared lib
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++14 -g -Ofast -fno-inline -Wall")
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG}  -std=gnu++14 -g -Ofast -fno-inline -Wall")


include_directories("../../../../common_includes/glm")
include_directories("../../../../common_includes")

add_library(NdkGlue SHARED
        GLES2Renderer.cpp
        LightningStrategy.cpp
        NativeBitmap.cpp
        NdkGlue.cpp
        Texture.cpp
)

# add lib dependencies
target_link_libraries(NdkGlue
        android
        jnigraphics
        log
        EGL
        OpenSLES
        GLESv2)

