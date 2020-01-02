package cn.cuilan.ssmp.admin.beans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class AdminInitializingBean implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Spring 启动...");
    }
}
