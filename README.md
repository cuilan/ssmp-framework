# ssmp-framework

SpringBoot-MybatisPlus脚手架

## 包含模块

- demo
- utils

## 自定义注解 @WebLog

基于 Spring AOP 环绕通知，作用于 Controller 层，将在 log 中输出接口请求、入参、出参、IP、耗时、方法调用栈等相关信息。

使用如下：

```java
@WebLog("接口描述信息")
@GetMapping("/hello")
public String getUser() {
    return "Hello World!";
}
``` 
