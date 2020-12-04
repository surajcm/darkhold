#!/bin/bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-15.0.1.jdk/Contents/Home"

#export JAVA_HOME="/usr/lib/jvm/java-14-openjdk-amd64/"

export JAVA_OPTS="-Xms512m -Xmx512m"
exec ./gradlew "$@"
