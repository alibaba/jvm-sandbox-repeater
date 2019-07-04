#!/usr/bin/env bash
# exit shell with err_code
# $1 : err_code
# $2 : err_msg

SERVICE_OUT="${HOME}/repeater-bootstrap.log"

exit_on_err()
{
    [[ ! -z "${2}" ]] && echo "${2}" 1>&2
    exit ${1}
}

check(){
    local expireTime=0
    local time=300
    while true
    do
        ret=`fgrep "Started Application in " ${SERVICE_OUT}`
        if [ -z "${ret}" ]; then
            sleep 1
            expireTime=`expr ${expireTime} + 1`
            if [ ${expireTime} -gt ${time} ]; then
                echo -n -e "\rWait Application Start: cost ${expireTime}s."
                exit_on_err 1 "\rApplication star failed"
            else
                echo -n -e "\rWait Application Start: cost ${expireTime}s."
            fi
        else
           echo "Application Start Success cost ${expireTime}s."
           exit 0;
        fi
    done
}

check