#!/usr/bin/env bash

typeset SANDBOX_HOME=${HOME}/sandbox

typeset MODULE_HOME=${HOME}/.sandbox-module

# exit shell with err_code
# $1 : err_code
# $2 : err_msg
exit_on_err()
{
    [[ ! -z "${2}" ]] && echo "${2}" 1>&2
    exit ${1}
}

main(){
    echo "======  begin to install sandbox and repeater module       ======";
    echo "======  step 0 begin to download sandbox package           ======";
    curl -s https://github.com/alibaba/jvm-sandbox-repeater/releases/download/v1.0.0/sandbox-1.3.3-bin.tar | tar xz -C ${HOME} || exit_on_err 1 "extract sandbox failed"
    echo "======  step 1 begin to download repeater module package   ======";
    if [ ! -d ${MODULE_HOME} ]; then
        mkdir -p ${MODULE_HOME} || exit_on_err 1 "permission denied mkdir ${MODULE_HOME}"
    fi
    curl -s https://github.com/alibaba/jvm-sandbox-repeater/releases/download/v1.0.0/repeater-stable-bin.tar | tar xz -C ${MODULE_HOME} || exit_on_err 1 "extract repeater failed"
    echo "======                 install finished                    ======";
}

main