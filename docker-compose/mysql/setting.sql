
-- 修改数据库密码加密方式，修改root密码
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456';

-- 刷新权限
FLUSH PRIVILEGES;
