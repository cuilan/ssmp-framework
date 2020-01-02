package cn.cuilan.ssmp.admin.config;

import cn.cuilan.ssmp.admin.beans.AdminInitializingBean;
import cn.cuilan.ssmp.admin.security.CurrentLoginUserGetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
@Configuration
public class BeanConfig {

    /**
     * 当前登录用户获取器
     */
    @Bean
    public CurrentLoginUserGetter currentLoginUserGetter() {
        return new CurrentLoginUserGetter();
    }

    /**
     * BCrypt加密器
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AdminInitializingBean adminInitializingBean() {
        return new AdminInitializingBean();
    }
}
