#!/usr/bin/env bash

# repeater's target dir
REPEATER_TARGET_DIR=../target/repeater

# exit shell with err_code
# $1 : err_code
# $2 : err_msg
exit_on_err()
{
    [[ ! -z "${2}" ]] && echo "${2}" 1>&2
    exit ${1}
}

# package
sh ./package.sh || exit_on_err 1 "install failed cause package failed"

# extract sandbox to ${HOME}
curl -s https://github.com/alibaba/jvm-sandbox-repeater/releases/download/v1.0.0/sandbox-1.3.3-bin.tar | tar xz -C ${HOME} || exit_on_err 1 "extract sandbox failed"

# copy module to ~/.sandbox-module
mkdir -p ${HOME}/.sandbox-module || exit_on_err 1 "permission denied, can not mkdir ~/.sandbox-module"
cp -r ${REPEATER_TARGET_DIR}/* ${HOME}/.sandbox-module  || exit_on_err 1 "permission denied, can not copy module to ~/.sandbox-module"