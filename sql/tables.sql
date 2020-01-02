-- t_user
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`          INT(20)      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `username`    VARCHAR(32)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(128) NULL DEFAULT NULL COMMENT '密码',
    `real_name`   VARCHAR(32) COMMENT '真实姓名',
    `gender`      TINYINT(2)   NOT NULL COMMENT '性别，0-男，1-女',
    `age`         INT(11)      NULL DEFAULT NULL COMMENT '年龄',
    `create_time` BIGINT(20) COMMENT '创建时间',
    `update_time` BIGINT(20) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '用户表';

-- 系统用户表
CREATE TABLE `t_sys_user`
(
    `id`              int(11) UNSIGNED    NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `phone`           varchar(20)                  DEFAULT NULL COMMENT '手机号',
    `email`           varchar(255)                 DEFAULT NULL COMMENT '邮箱',
    `username`        varchar(32)         NOT NULL COMMENT '用户名',
    `password`        varchar(255)        NOT NULL COMMENT '用户密码',
    `is_admin`        tinyint(1) UNSIGNED NOT NULL DEFAULT '1' COMMENT '是否为超级管理员，不允许修改信息',
    `status`          tinyint(1) UNSIGNED NOT NULL DEFAULT '1' COMMENT '0-停用 1-启用',
    `full_name`       varchar(20)         NOT NULL DEFAULT '' COMMENT '真实姓名',
    `portrait`        varchar(255)        NOT NULL DEFAULT '' COMMENT '头像',
    `reserved_info`   varchar(255)                 DEFAULT NULL COMMENT '预留信息',
    `tmp_pwd`         varchar(255)        NOT NULL COMMENT '用户临时密码',
    `trash_tmp_pwd`   tinyint(1) UNSIGNED NOT NULL DEFAULT '0' COMMENT '废弃临时密码(0-未作废,1-已作废)',
    `notes`           varchar(255)                 DEFAULT NULL COMMENT '备注',
    `created_by`      bigint(11)                   DEFAULT NULL COMMENT '添加者ID',
    `last_login_ip`   varchar(100)                 DEFAULT NULL COMMENT '最后登录IP',
    `last_login_time` bigint(13)                   DEFAULT NULL COMMENT '最后登录时间',
    `create_time`     bigint(13)          NOT NULL COMMENT '创建时间',
    `update_time`     bigint(13)                   DEFAULT NULL COMMENT '最后修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ix_tb_sys_user_username` (`username`),
    UNIQUE KEY `phone` (`phone`),
    UNIQUE KEY `email` (`email`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='系统用户表';


-- 系统菜单表
CREATE TABLE `t_sys_menu`
(
    `id`          INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(200) DEFAULT NULL COMMENT '菜单名称',
    `url`         VARCHAR(255) DEFAULT NULL COMMENT '跳转地址',
    `icon`        VARCHAR(32)  DEFAULT NULL COMMENT '菜单图标儿',
    `parent_id`   INT(11)      DEFAULT NULL COMMENT '父菜单 ID',
    `priority`    SMALLINT(6)  DEFAULT '0' COMMENT '优先级，越小，同级显示的时候越靠前',
    `note`        VARCHAR(255) DEFAULT NULL COMMENT '菜单备注',
    `create_time` BIGINT(13)   DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT(13)   DEFAULT NULL COMMENT '最后修改时间',
    `visible`     INT(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='系统菜单表';


-- 系统角色表
CREATE TABLE `t_sys_role`
(
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '角色Id',
    `name`        VARCHAR(255) DEFAULT NULL COMMENT '角色名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `create_time` BIGINT(13)   DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT(13)   DEFAULT NULL COMMENT '最后修改时间',
    `visible`     INT(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='系统角色表';


-- 系统权限表
CREATE TABLE `t_sys_permission`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'permissionId',
    `name`        varchar(255) DEFAULT NULL COMMENT '权限名称',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `create_time` bigint(13)   DEFAULT NULL COMMENT '创建时间',
    `update_time` bigint(13)   DEFAULT NULL COMMENT '最后修改时间',
    `visible`     int(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8 COMMENT ='系统权限表';


-- 用户角色关联表
CREATE TABLE `t_sys_user_roles`
(
    `roles_id`   bigint(20) NOT NULL,
    `sys_user_id` bigint(20) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='用户和角色关联表';
-- 联合唯一索引
ALTER TABLE `t_sys_user_roles`
    ADD UNIQUE INDEX (`roles_id`, `sys_user_id`);


-- 权限角色关联表
CREATE TABLE `t_sys_permission_roles`
(
    `roles_id`          bigint(20) NOT NULL,
    `sys_permission_id` bigint(20) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色和权限关联表';
-- 联合唯一索引
ALTER TABLE `t_sys_permission_roles`
    ADD UNIQUE INDEX (`roles_id`, `sys_permission_id`);


-- 菜单角色关联表
CREATE TABLE `t_sys_menu_roles`
(
    `roles_id`    BIGINT(20) NOT NULL,
    `sys_menu_id` BIGINT(20) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='角色和菜单关联表';
-- 联合唯一索引
ALTER TABLE `t_sys_menu_roles`
    ADD UNIQUE INDEX (`roles_id`, `sys_menu_id`);


-- 系统操作日志表
CREATE TABLE `t_sys_operation_log`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `operation_type` varchar(255)        NOT NULL COMMENT '操作类型',
    `description`    varchar(255) DEFAULT NULL COMMENT '描述',
    `sys_user_id`    bigint(20)          NOT NULL COMMENT '操作人',
    `class_type`     varchar(255)        NOT NULL COMMENT '被操作的Java类',
    `operated_id`    bigint(20)   DEFAULT NULL COMMENT '被操作id',
    `create_time`    bigint(20)          NOT NULL COMMENT '创建时间',
    `update_time`    bigint(20)          NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='操作日志表';

