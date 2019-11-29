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

# maven package the sandbox
mvn clean package -Dmaven.test.skip=true -f ../pom.xml || exit_on_err 1 "package repeater failed."

mkdir -p ${REPEATER_TARGET_DIR}/plugins
mkdir -p ${REPEATER_TARGET_DIR}/cfg

cp ./repeater-logback.xml ${REPEATER_TARGET_DIR}/cfg/repeater-logback.xml \
    && cp ./repeater.properties ${REPEATER_TARGET_DIR}/cfg/repeater.properties \
    && cp ./repeater-config.json ${REPEATER_TARGET_DIR}/cfg/repeater-config.json \
    && cp ../repeater-module/target/repeater-module-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/repeater-module.jar \
    && cp ../repeater-console/repeater-console-start/target/repeater-console.jar ${REPEATER_TARGET_DIR}/repeater-bootstrap.jar \
    && cp ../repeater-plugins/ibatis-plugin/target/ibatis-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/ibatis-plugin.jar \
    && cp ../repeater-plugins/java-plugin/target/java-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/java-plugin.jar \
    && cp ../repeater-plugins/mybatis-plugin/target/mybatis-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/mybatis-plugin.jar \
    && cp ../repeater-plugins/dubbo-plugin/target/dubbo-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/dubbo-plugin.jar \
    && cp ../repeater-plugins/redis-plugin/target/redis-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/redis-plugin.jar \
    && cp ../repeater-plugins/http-plugin/target/http-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/http-plugin.jar \
    && cp ../repeater-plugins/hibernate-plugin/target/hibernate-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/hibernate-plugin.jar \
    && cp ../repeater-plugins/spring-data-jpa-plugin/target/spring-data-jpa-plugin-*-jar-with-dependencies.jar ${REPEATER_TARGET_DIR}/plugins/spring-data-jpa-plugin.jar