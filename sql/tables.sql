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

