package cn.cuilan.ssmp.admin.config;

import cn.cuilan.ssmp.admin.security.AuthenticationTokenFilter;
import cn.cuilan.ssmp.admin.security.CustomAuthenticationFilter;
import cn.cuilan.ssmp.admin.security.UserAuthenticationProvider;
import cn.cuilan.ssmp.admin.security.handler.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Spring Security 安全配置
 *
 * @author zhang.yan
 * @date 2019-12-30
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 用户验证入口，全部处理为：未登录
     */
    @Resource
    private UserAuthenticationEntryPointHandler userAuthenticationEntryPointHandler;

    /**
     * 登录失败的处理
     */
    @Resource
    private LoginFailureHandler loginFailureHandler;

    /**
     * 登录成功的处理
     */
    @Resource
    private LoginSuccessHandler loginSuccessHandler;

    /**
     * 用户登出成处理
     */
    @Resource
    private UserLogoutSuccessHandler userLogoutSuccessHandler;

    /**
     * 用户没有权限访问的处理
     */
    @Resource
    private UserAccessDeniedHandler userAccessDeniedHandler;

    /**
     * 登录逻辑验证
     */
    @Resource
    private UserAuthenticationProvider userAuthenticationProvider;

    // -------------------------------------------------------------------------

    /**
     * 认证token过滤器
     */
    @Bean
    public AuthenticationTokenFilter authenticationTokenFilter() throws Exception {
        return new AuthenticationTokenFilter(authenticationManager());
    }

    /**
     * 自定义认证过滤器
     */
    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        filter.setAuthenticationFailureHandler(loginFailureHandler);
        filter.setFilterProcessesUrl("/api/admin/login");
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }

    // -------------------------------------------------------------------------

    /**
     * 配置登录验证逻辑
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        // 启用自定义的登录验证逻辑
        auth.authenticationProvider(userAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭基础验证
        // http.httpBasic().disable();

        http.authorizeRequests()
                // 不进行权限验证的请求
                .antMatchers("/health").permitAll()
                .antMatchers("/api/admin/login").permitAll()
                // 其他请求需要登录后才能访问
                .anyRequest().authenticated()
                .and()
                // 配置未登录自定义处理类
                .httpBasic().authenticationEntryPoint(userAuthenticationEntryPointHandler)
                .and()
                // 配置登出地址
                .logout().logoutUrl("/api/admin/logout")
                // 配置登出自定义处理类
                .logoutSuccessHandler(userLogoutSuccessHandler)
                .and()
                // 配置没有权限自定义处理类
                .exceptionHandling().accessDeniedHandler(userAccessDeniedHandler)
                .and()
                // 取消防跨域请求
                .csrf().disable();

        // 重写Filler从body中获取
        http.addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        // 不需要session管理
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 禁用缓存
        http.headers().cacheControl();
        // 添加token过滤器
        http.addFilter(authenticationTokenFilter());
    }
}
