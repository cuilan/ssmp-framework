# ssmp-framework

SpringBoot-MybatisPlus脚手架

## 包含模块

- base
- <a href="#admin">ssmp-admin 管理系统</a>
- <a href="#service">ssmp-service 核心服务</a>
- <a href="#utils">ssmp-utils 工具集</a>

---

### <a name="admin">ssmp-admin 管理系统</a>

* 集成 `Spring Security` 用户、角色、权限、菜单等功能。
* 支持方法级别的安全拦截。

#### 自定义注解 @WebLog

基于 `Spring AOP` 环绕通知，作用于 `Controller` 层，将在 log 中输出接口请求、入参、出参、IP、耗时、方法调用栈等相关信息。

使用如下：

```java
@RestController
public class HelloController {
    @WebLog("接口描述信息")
    @GetMapping("/hello")
    public String seyHello() {
        return "Hello World!";
    }
}
``` 

---

### <a name="service">ssmp-service 核心服务</a>

* 集成 `Mybatis Plus` 持久层框架.
* 数据观察者组件等.

---

### <a name="utils">ssmp-utils 工具集</a>

工具类集合:
* encrypt: 加密、签名、信息摘要等算法等.
* lock: Redis分布式锁等.
