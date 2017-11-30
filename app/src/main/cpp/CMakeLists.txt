# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.
# build application's shared lib

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_library( # Specifies the name of the library.
             dawgdic-build

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             dawgdic-build.cc )

add_library( # Specifies the name of the library.
             dawgdic-find

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             dawgdic-find.cc )

find_library( # Defines the name of the path variable that stores the
              # location of the NDK library.
              log-lib

              # Specifies the name of the NDK library that
              # CMake needs to locate.
              log )

# Links your native library against one or more other native libraries.
target_link_libraries( # Specifies the target library.
                       dawgdic-build

                       # Links the log library to the target library.
                       ${log-lib} )


# Specifies a path to native header files.
#[[ include_directories (src/main/cpp/include/)]]
include_directories (.)