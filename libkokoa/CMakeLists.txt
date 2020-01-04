cmake_minimum_required(VERSION 3.14)
project(kokoa C)

set(CMAKE_C_STANDARD 11)

add_library(kokoa SHARED library.c)
