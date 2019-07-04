CREATE DATABASE IF NOT EXISTS repeater
  DEFAULT CHARSET utf8
  COLLATE utf8_general_ci;
DROP TABLE IF EXISTS recordModel;
CREATE TABLE recordModel (
  id             BIGINT(20)   NOT NULL AUTO_INCREMENT PRIMARY KEY
  COMMENT '主键',
  gmt_create     DATETIME     NOT NULL
  COMMENT '创建时间',
  gmt_record     DATETIME     NOT NULL
  comment '录制时间',
  app_name       VARCHAR(255) NOT NULL
  COMMENT '应用名',
  environment    VARCHAR(255) NOT NULL
  COMMENT '环境信息',
  host           VARCHAR(36)  NOT NULL
  COMMENT '机器IP',
  trace_id       VARCHAR(32)  NOT NULL
  COMMENT '链路追踪ID',
  wrapper_record LONGTEXT     NOT NULL
  COMMENT '记录序列化信息'
)
  ENGINE = InnoDB
  COMMENT = '录制信息'
  DEFAULT CHARSET = utf8
  AUTO_INCREMENT = 1;