cmake_minimum_required(VERSION 3.0.0 FATAL_ERROR)

project(BredholtPlugins)

set(EXTENSIONS_DIR ${CMAKE_BINARY_DIR}/BredholtPlugins/Extensions)
set(PLUGINS_DIR ${CMAKE_BINARY_DIR}/BredholtPlugins/Plugins)

set(SC_PATH ${PROJECT_SOURCE_DIR}/supercollider)

add_subdirectory(Plugins/PV_RandPhase)

option(HAS_FAUST "Build Faust plugins" OFF)
if (HAS_FAUST)
  add_subdirectory(Plugins/MonoPhaser)
endif()

install(DIRECTORY ${PROJECT_SOURCE_DIR}/Extensions/ DESTINATION ${EXTENSIONS_DIR})

option(HAS_QT "Build GUI plugins" OFF)
if (HAS_QT)
  install(DIRECTORY ${PROJECT_SOURCE_DIR}/GuiExtensions/ DESTINATION ${EXTENSIONS_DIR})
endif()
