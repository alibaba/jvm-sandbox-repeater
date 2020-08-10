CREATE DATABASE IF NOT EXISTS repeater2
    DEFAULT CHARSET utf8
    COLLATE utf8_general_ci;

use repeater2;



DROP TABLE IF EXISTS app;
CREATE TABLE app
(
    id             BIGINT(20)    NOT NULL AUTO_INCREMENT PRIMARY KEY
        COMMENT '主键',
    name       VARCHAR(255)  NOT NULL
        COMMENT '应用名',
    memo       VARCHAR(255)  NOT NULL
        COMMENT '备注',
    gmt_create      DATETIME     NOT NULL
        COMMENT '创建时间',
    gmt_modified    DATETIME     NOT NULL
        comment '修改时间'
)
    ENGINE = InnoDB
    COMMENT = '录制信息'
    DEFAULT CHARSET = utf8
    AUTO_INCREMENT = 1;



DROP TABLE IF EXISTS module_config;
CREATE TABLE `module_config` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `app_id` bigint(20) NOT NULL COMMENT '应用名',
   `environment` varchar(255) NOT NULL COMMENT '环境信息',
   `config` longtext NOT NULL COMMENT '配置信息',
   `gmt_create` datetime NOT NULL COMMENT '创建时间',
   `gmt_modified` datetime NOT NULL COMMENT '录制时间',
   PRIMARY KEY (`id`),
   KEY `fk_mc_app_id_idx` (`app_id`),
   CONSTRAINT `fk_mc_app_id` FOREIGN KEY (`app_id`) REFERENCES `app` (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模块配置信息';



DROP TABLE IF EXISTS module_info;
CREATE TABLE `module_info` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
   `ip` varchar(36) NOT NULL COMMENT '机器IP',
   `port` varchar(12) NOT NULL COMMENT '链路追踪ID',
   `username` varchar(12) NOT NULL COMMENT '用户名',
   `password` varchar(30) NULL COMMENT '密码',
   `private_rsa_file` varchar(80) NOT NULL COMMENT '密钥文件',
   `status` varchar(36) NOT NULL COMMENT '模块状态',
   `module_config_id` bigint(20) NOT NULL COMMENT '环境配置',
   `gmt_create` datetime NOT NULL COMMENT '创建时间',
   `gmt_modified` datetime NOT NULL COMMENT '修改时间',
   PRIMARY KEY (`id`),
   KEY `fk_mi_config_id_idx` (`module_config_id`),
   CONSTRAINT `fk_mi_config_id` FOREIGN KEY (`module_config_id`) REFERENCES `module_config` (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='在线模块信息';


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


ALTER TABLE `module_info`
ADD COLUMN `pre_command` VARCHAR(500) NULL AFTER `private_rsa_file`;





---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------
---------------------------------







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



