#!/usr/bin/env bash
if [ -d "$HOME/.jdks/temurin-17" ]; then
    export JAVA_HOME="$HOME/.jdks/temurin-17"
elif [ -d "/opt/android-studio/jbr" ]; then
    export JAVA_HOME="/opt/android-studio/jbr"
fi
exec /home/kimmi/.gradle/wrapper/dists/gradle-8.11.1-bin/bpt9gzteqjrbo1mjrsomdt32c/gradle-8.11.1/bin/gradle "$@"
