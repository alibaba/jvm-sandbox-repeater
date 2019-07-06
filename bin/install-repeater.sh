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
    curl -s http://sandbox-ecological.oss-cn-hangzhou.aliyuncs.com/sandbox-1.2.1-bin.tar | tar xz -C ${HOME} || exit_on_err 1 "extract sandbox failed"
    echo "======  step 1 begin to download repeater module package   ======";
    if [ ! -d ${MODULE_HOME} ]; then
        mkdir -p ${MODULE_HOME} || exit_on_err 1 "permission denied mkdir ${MODULE_HOME}"
    fi
    curl -s http://sandbox-ecological.oss-cn-hangzhou.aliyuncs.com/repeater-stable-bin.tar | tar xz -C ${MODULE_HOME} || exit_on_err 1 "extract repeater failed"
    echo "======                 install finished                    ======";
}

main