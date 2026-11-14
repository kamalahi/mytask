# R8 shrinking rules for the release build.
#
# Compose, AndroidX and Kotlin all ship their own rules inside their libraries,
# and R8 picks those up by itself, so this file stays empty for a project this
# size. If a release build ever crashes with a ClassNotFoundException that the
# debug build does not have, the fix is a -keep line here naming that class.
