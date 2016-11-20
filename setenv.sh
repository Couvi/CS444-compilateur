#!/usr/bin/env bash

ABS_ROOT_DIR=`pwd`
export CLASSPATH=$ABS_ROOT_DIR:$ABS_ROOT_DIR/ProjetCompil/Global/Bin/java-cup-11a-runtime.jar:$ABS_ROOT_DIR/ProjetCompil/Global/Bin/JFlex.jar:.
export PATH=$PATH:$ABS_ROOT_DIR/ProjetCompil/Bin
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$ABS_ROOT_DIR/ProjetCompil/Bin/libs-ima

