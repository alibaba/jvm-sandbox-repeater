#!/usr/bin/env bash

# simulator travis build

cd ./bin/

# boot repeater console
rm -rf ${HOME}/sandbox ${HOME}/.sandbox-module

nohup sh ./bootstrap.sh > ${HOME}/repeater-bootstrap.log &

sh ./health.sh

cd -

# run cobertura

mvn clean cobertura:cobertura

kill -9 $(ps -ef | grep "repeater-bootstrap.jar" | grep "java" | grep -v grep | awk '{print $2}')

bash <(curl -s https://codecov.io/bash) -t acfa5b67-54e4-45a2-875e-6f2cf79fe181