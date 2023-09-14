
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 app
# ------------------------------------------------------------

CREATE TABLE `app` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `app` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用名',
  `app_id` bigint DEFAULT NULL COMMENT '应用id',
  `bu` int(10) unsigned zerofill DEFAULT NULL COMMENT '归属bu',
  `version` tinyint NOT NULL DEFAULT '0' COMMENT '0: jdk8以下 1：JDK17',
  `region` varchar(20) DEFAULT NULL COMMENT '区域，CN中国，EU海外',
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_appName_IDX` (`app`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# 转储表 app_bu
# ------------------------------------------------------------

CREATE TABLE `app_bu` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `pid` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 app_config
# ------------------------------------------------------------

CREATE TABLE `app_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app` varchar(100) NOT NULL,
  `type` tinyint NOT NULL COMMENT '类型',
  `env` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT 'all',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `config` text NOT NULL COMMENT '配置',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用配置表';



# 转储表 app_groovy_config
# ------------------------------------------------------------

CREATE TABLE `app_groovy_config` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '应用名',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型',
  `status` tinyint DEFAULT '0' COMMENT '0:待生效 1:生效',
  `user` varchar(20) DEFAULT NULL COMMENT '创建人',
  `name` varchar(50) DEFAULT NULL COMMENT '脚本名称',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL COMMENT '更新时间',
  `env` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '适用环境',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '内容',
  PRIMARY KEY (`id`),
  KEY `ap` (`app`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 module_config
# ------------------------------------------------------------

CREATE TABLE `module_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录制时间',
  `app_name` varchar(255) NOT NULL COMMENT '应用名',
  `environment` varchar(255) NOT NULL COMMENT '环境信息',
  `config` longtext NOT NULL COMMENT '配置信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='模块配置信息';



# 转储表 module_info
# ------------------------------------------------------------

CREATE TABLE `module_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(255) NOT NULL COMMENT '应用名',
  `environment` varchar(255) NOT NULL COMMENT '环境信息',
  `ip` varchar(36) NOT NULL COMMENT '机器IP',
  `port` varchar(12) NOT NULL COMMENT '链路追踪ID',
  `version` varchar(128) NOT NULL COMMENT '模块版本号',
  `status` varchar(36) NOT NULL COMMENT '模块状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='在线模块信息';



# 转储表 record
# ------------------------------------------------------------

CREATE TABLE `record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_record` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录制时间',
  `app_name` varchar(255) NOT NULL COMMENT '应用名',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `environment` varchar(255) NOT NULL COMMENT '环境信息',
  `host` varchar(36) NOT NULL COMMENT '机器IP',
  `trace_id` varchar(32) NOT NULL COMMENT '链路追踪ID',
  `entrance_desc` varchar(2000) NOT NULL COMMENT '链路追踪ID',
  `wrapper_record` longtext NOT NULL COMMENT '记录序列化信息',
  `add` tinyint NOT NULL DEFAULT '0' COMMENT '是否添加过',
  `request` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求参数JSON',
  `response` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '返回值JSON',
  `extend` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '扩展信息',
  `version` tinyint NOT NULL DEFAULT '0' COMMENT '版本信息',
  PRIMARY KEY (`id`),
  KEY `app_name` (`app_name`,`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='录制信息';



# 转储表 record_body
# ------------------------------------------------------------

CREATE TABLE `record_body` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '消息key',
  `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '消息body',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 record_case
# ------------------------------------------------------------

CREATE TABLE `record_case` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `case_id` varchar(50) NOT NULL COMMENT '用例id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_record` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录制时间',
  `app_name` varchar(255) NOT NULL COMMENT '应用名',
  `environment` varchar(255) NOT NULL COMMENT '环境信息',
  `host` varchar(36) NOT NULL COMMENT '机器IP',
  `trace_id` varchar(32) NOT NULL COMMENT '链路追踪ID',
  `entrance_desc` varchar(2000) NOT NULL COMMENT '链路追踪ID',
  `wrapper_record` longtext NOT NULL COMMENT '记录序列化信息',
  `request` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求参数JSON',
  `response` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '返回值JSON',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='录制信息';



# 转储表 replay
# ------------------------------------------------------------

CREATE TABLE `replay` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(255) NOT NULL COMMENT '应用名',
  `environment` varchar(255) NOT NULL COMMENT '环境信息',
  `ip` varchar(36) NOT NULL COMMENT '机器IP',
  `repeat_id` varchar(32) NOT NULL COMMENT '回放ID',
  `status` tinyint NOT NULL COMMENT '回放状态',
  `trace_id` varchar(32) DEFAULT NULL COMMENT '链路追踪ID',
  `cost` bigint DEFAULT NULL COMMENT '回放耗时',
  `diff_result` longtext COMMENT 'diff结果',
  `response` longtext COMMENT '回放结果',
  `mock_invocation` longtext COMMENT 'mock过程',
  `success` bit(1) DEFAULT NULL COMMENT '是否回放成功',
  `record_id` bigint DEFAULT NULL COMMENT '外键',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '0: 采集回放 1: 用例回放',
  `case_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT 'case_id',
  PRIMARY KEY (`id`),
  KEY `replay_app_name_IDX` (`app_name`,`environment`,`repeat_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='回放信息';



# 转储表 resource
# ------------------------------------------------------------

CREATE TABLE `resource` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `rid` bigint NOT NULL COMMENT '资源id',
  `user` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `role` tinyint DEFAULT NULL COMMENT '角色',
  `type` tinyint NOT NULL COMMENT '资源类型',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '删除标志',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `extend` varchar(500) DEFAULT NULL COMMENT '扩展信息',
  PRIMARY KEY (`id`),
  KEY `rid_user` (`rid`,`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 t_pub_sequence
# ------------------------------------------------------------

DROP TABLE IF EXISTS `t_pub_sequence`;

CREATE TABLE `t_pub_sequence` (
  `SEQ_NAME` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '序列名称',
  `SEQ_VALUE` bigint NOT NULL COMMENT '目前序列值',
  `MIN_VALUE` bigint NOT NULL COMMENT '最小值',
  `MAX_VALUE` bigint NOT NULL COMMENT '最大值',
  `STEP` bigint NOT NULL COMMENT '每次取值的数量',
  `TM_CREATE` datetime NOT NULL COMMENT '创建时间',
  `TM_SMP` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='流水号生成表';



# 转储表 tag_config
# ------------------------------------------------------------

CREATE TABLE `tag_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_name` varchar(50) NOT NULL COMMENT '应用',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `nick` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '别名，中文名',
  `scope` int DEFAULT NULL COMMENT '作用域',
  `identity` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '流量id',
  `jsonpath` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'jsonpath取值路径',
  PRIMARY KEY (`id`),
  KEY `app_name` (`app_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 task
# ------------------------------------------------------------

CREATE TABLE `task` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `app_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '应用名称',
  `biz_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '幂等字段',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_update` datetime NOT NULL COMMENT '更新时间',
  `gmt_start` datetime NOT NULL COMMENT '开始时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `type` tinyint unsigned DEFAULT NULL COMMENT '任务类型',
  `env` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '环境',
  `creator` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '创建人',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `extend` varchar(1024) DEFAULT NULL COMMENT '扩展信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_biz` (`app_name`,`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 task_item
# ------------------------------------------------------------

CREATE TABLE `task_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_update` datetime NOT NULL COMMENT '更新时间',
  `task_id` bigint NOT NULL COMMENT '任务id',
  `type` tinyint NOT NULL COMMENT '类型',
  `status` tinyint NOT NULL COMMENT '状态',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '名称',
  `extend` varchar(1024) DEFAULT NULL COMMENT '扩展信息',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  `exec_time` int NOT NULL DEFAULT '0' COMMENT '执行次数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `task_name` (`task_id`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 test_case
# ------------------------------------------------------------

CREATE TABLE `test_case` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `case_id` varchar(50) NOT NULL,
  `case_name` varchar(100) NOT NULL,
  `suit_id` bigint NOT NULL,
  `record_id` bigint NOT NULL COMMENT '对应的caseId',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_record` datetime NOT NULL COMMENT '录制时间',
  `app_name` varchar(255) NOT NULL COMMENT '应用名',
  `environment` varchar(255) NOT NULL COMMENT '环境信息',
  `user` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户',
  `host` varchar(36) NOT NULL COMMENT '机器IP',
  `trace_id` varchar(32) NOT NULL COMMENT '链路追踪ID',
  `entrance_desc` varchar(2000) NOT NULL COMMENT '链路追踪ID',
  `request_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '唯一请求id',
  `delete` tinyint DEFAULT '0' COMMENT '是否删除',
  `extend` varchar(1000) DEFAULT NULL COMMENT '扩展信息',
  `recurrence` tinyint DEFAULT '0' COMMENT '是否全网回归',
  PRIMARY KEY (`id`),
  UNIQUE KEY `test_case_case_id_IDX` (`case_id`) USING BTREE,
  KEY `app_suit` (`app_name`,`suit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='录制信息';



# 转储表 test_case_config
# ------------------------------------------------------------

CREATE TABLE `test_case_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `case_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用例id',
  `type` tinyint NOT NULL COMMENT '类型',
  `config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置内容',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_case_type` (`case_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 test_suit
# ------------------------------------------------------------

CREATE TABLE `test_suit` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `parent_id` bigint unsigned NOT NULL COMMENT '父id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` tinyint NOT NULL COMMENT '是否根节点',
  `app_name` varchar(50) DEFAULT NULL,
  `extend` varchar(500) DEFAULT NULL COMMENT '扩展信息',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='测试套件';



# 转储表 trx_msg
# ------------------------------------------------------------

CREATE TABLE `trx_msg` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL COMMENT '更新时间',
  `topic` varchar(30) NOT NULL COMMENT '主题',
  `gmt_exec` datetime NOT NULL COMMENT '运行时间',
  `exec_time` int NOT NULL COMMENT '运行次数',
  `content` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '扩展信息',
  `group` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '分组',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
