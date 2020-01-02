
-- 系统用户初始数据
INSERT INTO `t_sys_user` VALUES
('1', '17700001111', '123456@qq.com', 'admin', '$2a$10$X1gEcQgOALmrDEUoClXl8.i7Qi055pnGLUuWb70V7tu8ZrebB749S', '1', '1',
'超级管理员', '', null, '123456', '0', null, null, '10.10.2.221', '1573893300000', '1573893300000', '1573893300000'
 );

-- 系统角色初始数据
INSERT INTO `t_sys_role` VALUES ('1', 'ROLE_ROOT', '超级管理员', '1573893300000', '1575373179607', '1');
INSERT INTO `t_sys_role` VALUES ('2', 'ROLE_ADMIN', '系统管理员', '1573893300000', '1575373179607', '1');
INSERT INTO `t_sys_role` VALUES ('3', 'ROLE_GUEST', '访客', '1573893300000', '1575373179607', '1');

-- 系统权限初始数据
INSERT INTO `t_sys_permission` VALUES ('1', 'user_add', '添加系统用户的权限', '1573893300000', '1575795008138', '1');
INSERT INTO `t_sys_permission` VALUES ('2', 'user_delete', '删除系统用户的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('3', 'user_update', '修改系统用户的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('4', 'user_query', '查询系统用户的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('5', 'role_add', '添加系统角色的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('6', 'role_update', '修改系统角色的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('7', 'role_delete', '删除系统角色的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('8', 'role_query', '查询系统角色的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('9', 'menu_add', '添加系统菜单的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('10', 'menu_update', '修改系统菜单的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('11', 'menu_delete', '删除系统菜单的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('12', 'menu_query', '查询系统菜单的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('13', 'permission_add', '添加系统权限的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('14', 'permission_update', '修改系统权限的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('15', 'permission_delete', '删除系统权限的权限', '1573893300000', '1573893300000', '1');
INSERT INTO `t_sys_permission` VALUES ('16', 'permission_query', '查询系统权限的权限', '1573893300000', '1573893300000', '1');

-- 系统菜单初始数据
INSERT INTO `t_sys_menu` VALUES ('1', '首页', '/', 'home', '0', '0', '首页，未来可以放一些数据展示', '1574224958539', '1575276068410', '1');
INSERT INTO `t_sys_menu` VALUES ('2', '系统管理', null, 'setting', '0', '1', '系统管理菜单，包括人员权限角色菜单等', '1574224958539', '1575276160526', '1');
INSERT INTO `t_sys_menu` VALUES ('3', '用户管理', '/system/users', 'user', '2', '0', null, '1574224958539', '1574224958539', '1');
INSERT INTO `t_sys_menu` VALUES ('4', '角色管理', '/system/roles', 'flag', '2', '1', null, '1574224958539', '1574224958539', '1');
INSERT INTO `t_sys_menu` VALUES ('5', '权限管理', null, null, '2', '2', null, '1574224958539', '1574224958539', '1');
INSERT INTO `t_sys_menu` VALUES ('6', '菜单管理', null, null, '2', '3', null, '1574224958539', '1574224958539', '1');
INSERT INTO `t_sys_menu` VALUES ('7', '部门管理', null, null, '2', '4', null, '1574224958539', '1574224958539', '1');
INSERT INTO `t_sys_menu` VALUES ('8', '操作纪录', '/system/logs', 'file-text', '2', '5', null, '1574224958539', '1574224958539', '1');

-- 关联关系
INSERT INTO `t_sys_user_roles` VALUES ('1', '1');
INSERT INTO `t_sys_user_roles` VALUES ('2', '1');
INSERT INTO `t_sys_user_roles` VALUES ('3', '1');

INSERT INTO `t_sys_menu_roles` VALUES ('1', '1');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '2');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '3');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '4');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '5');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '6');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '7');
INSERT INTO `t_sys_menu_roles` VALUES ('1', '8');

INSERT INTO `t_sys_permission_roles` VALUES ('1', '1');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '2');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '3');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '4');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '5');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '6');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '7');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '8');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '9');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '10');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '11');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '12');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '13');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '14');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '15');
INSERT INTO `t_sys_permission_roles` VALUES ('1', '16');