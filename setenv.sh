#!/usr/bin/env bash

ABS_ROOT_DIR=$(realpath `dirname "$0"`)
export CLASSPATH=$ABS_ROOT_DIR:$ABS_ROOT_DIR/ProjetCompil/Global/Bin/java-cup-11a-runtime.jar:$ABS_ROOT_DIR/ProjetCompil/Global/Bin/JFlex.jar:.
