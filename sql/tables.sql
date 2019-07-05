-- user
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id`        INT(20)      NOT NULL    AUTO_INCREMENT
  COMMENT '主键id',
  `real_name` VARCHAR(32)  NOT NULL
  COMMENT '真实姓名',
  `password`  VARCHAR(128) NULL        DEFAULT NULL
  COMMENT '密码',
  `age`       INT(11)      NULL        DEFAULT NULL
  COMMENT '年龄',
  PRIMARY KEY (`id`),
  KEY `real_name` (`real_name`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;

