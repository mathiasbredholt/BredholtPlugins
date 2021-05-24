set(PROJECT "MonoPhaser")
project(${PROJECT})

if (PLUGINS_DIR)
    set(INSTALL_DESTINATION "${PLUGINS_DIR}/${PROJECT}")
    set(INSTALL_DESTINATION_DISTRO "${PLUGINS_DIR}/${PROJECT}")
else()
    set(INSTALL_DESTINATION "${CMAKE_BINARY_DIR}/${PROJECT}")
    set(INSTALL_DESTINATION_DISTRO "${CMAKE_BINARY_DIR}/${PROJECT}")
endif()

message(${INSTALL_DESTINATION})

set(PLUGIN_DIR "plugins")

add_custom_target(MonoPhaser ALL
                  DEPENDS MonoPhaser.sc MonoPhaser.scx)

add_custom_command(OUTPUT MonoPhaser.sc MonoPhaser.scx
                   COMMAND
                   faust2supercollider ${CMAKE_CURRENT_SOURCE_DIR}/MonoPhaser.dsp -noprefix &&
                   mv ${CMAKE_CURRENT_SOURCE_DIR}/MonoPhaser.sc MonoPhaser.sc &&
                   mv ${CMAKE_CURRENT_SOURCE_DIR}/MonoPhaser.scx MonoPhaser.scx
                   COMMENT "Compiling MonoPhaser..."
                   DEPENDS MonoPhaser.dsp)
  

install(FILES ${CMAKE_CURRENT_BINARY_DIR}/MonoPhaser.scx
        DESTINATION ${INSTALL_DESTINATION}/${PLUGIN_DIR})

install(FILES ${CMAKE_CURRENT_BINARY_DIR}/MonoPhaser.sc
        DESTINATION ${INSTALL_DESTINATION}/sc/classes)

install(DIRECTORY "sc/" DESTINATION "${INSTALL_DESTINATION_DISTRO}"
        PATTERN "*")
