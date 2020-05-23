CREATE DATABASE IF NOT EXISTS repeater
    DEFAULT CHARSET utf8
    COLLATE utf8_general_ci;
DROP TABLE IF EXISTS record;
CREATE TABLE record
(
    id             BIGINT(20)    NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '主键',
    gmt_create     DATETIME      NOT NULL
        COMMENT '创建时间',
    gmt_record     DATETIME      NOT NULL
        comment '录制时间',
    app_name       VARCHAR(255)  NOT NULL
        COMMENT '应用名',
    environment    VARCHAR(255)  NOT NULL
        COMMENT '环境信息',
    host           VARCHAR(36)   NOT NULL
        COMMENT '机器IP',
    trace_id       VARCHAR(32)   NOT NULL
        COMMENT '链路追踪ID',
    entrance_desc  VARCHAR(2000) NOT NULL
        COMMENT '链路追踪ID',
    wrapper_record LONGTEXT      NOT NULL
        COMMENT '记录序列化信息',
    request        LONGTEXT      NOT NULL
        COMMENT '请求参数JSON',
    response       LONGTEXT      NOT NULL
        COMMENT '返回值JSON'
)
    ENGINE = InnoDB
    COMMENT = '录制信息'
    DEFAULT CHARSET = utf8
    AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS replay;
CREATE TABLE replay
(
    id              BIGINT(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '主键',
    gmt_create      DATETIME     NOT NULL
        COMMENT '创建时间',
    gmt_modified    DATETIME     NOT NULL
        comment '修改时间',
    app_name        VARCHAR(255) NOT NULL
        COMMENT '应用名',
    environment     VARCHAR(255) NOT NULL
        COMMENT '环境信息',
    ip              VARCHAR(36)  NOT NULL
        COMMENT '机器IP',
    repeat_id       VARCHAR(32)  NOT NULL
        COMMENT '回放ID',
    status          TINYINT      NOT NULL
        COMMENT '回放状态',
    trace_id        VARCHAR(32)
        COMMENT '链路追踪ID',
    cost            BIGINT(20)
        COMMENT '回放耗时',
    diff_result     LONGTEXT
        COMMENT 'diff结果',
    response        LONGTEXT
        COMMENT '回放结果',
    mock_invocation LONGTEXT
        COMMENT 'mock过程',
    success         BIT
        COMMENT '是否回放成功',
    record_id       BIGINT(20)
        COMMENT '外键'

)
    ENGINE = InnoDB
    COMMENT = '回放信息'
    DEFAULT CHARSET = utf8
    AUTO_INCREMENT = 1;


DROP TABLE IF EXISTS module_info;
CREATE TABLE module_info
(
    id           BIGINT(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '主键',
    gmt_create   DATETIME     NOT NULL
        COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL
        comment '修改时间',
    app_name     VARCHAR(255) NOT NULL
        COMMENT '应用名',
    environment  VARCHAR(255) NOT NULL
        COMMENT '环境信息',
    ip           VARCHAR(36)  NOT NULL
        COMMENT '机器IP',
    port         VARCHAR(12)  NOT NULL
        COMMENT '链路追踪ID',
    version      VARCHAR(128) NOT NULL
        COMMENT '模块版本号',
    status       VARCHAR(36)  NOT NULL
        COMMENT '模块状态'
)
    ENGINE = InnoDB
    COMMENT = '在线模块信息'
    DEFAULT CHARSET = utf8
    AUTO_INCREMENT = 1;


DROP TABLE IF EXISTS module_config;
CREATE TABLE module_config
(
    id           BIGINT(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '主键',
    gmt_create   DATETIME     NOT NULL
        COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL
        comment '录制时间',
    app_name     VARCHAR(255) NOT NULL
        COMMENT '应用名',
    environment  VARCHAR(255) NOT NULL
        COMMENT '环境信息',
    config       LONGTEXT     NOT NULL
        COMMENT '配置信息'
)
    ENGINE = InnoDB
    COMMENT = '模块配置信息'
    DEFAULT CHARSET = utf8
    AUTO_INCREMENT = 1;