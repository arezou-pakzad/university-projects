cmake_minimum_required(VERSION 3.12)
project(hw2)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)

add_executable(hw2
        httpserver.c
        libhttp.c
        libhttp.h
        Makefile
        utlist.h
        wq.c
        wq.h)
