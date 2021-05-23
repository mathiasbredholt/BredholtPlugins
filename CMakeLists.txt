cmake_minimum_required(VERSION 3.0.0 FATAL_ERROR)

project(Bredholt)

set(EXTENSIONS_DIR ${CMAKE_BINARY_DIR}/Bredholt/Extensions)
set(PLUGINS_DIR ${CMAKE_BINARY_DIR}/Bredholt/Plugins)

set(SC_PATH ${PROJECT_SOURCE_DIR}/supercollider)

add_subdirectory(Plugins/PV_RandPhase)

install(DIRECTORY ${PROJECT_SOURCE_DIR}/Extensions/ DESTINATION ${EXTENSIONS_DIR})
