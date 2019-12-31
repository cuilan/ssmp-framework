package cn.cuilan.ssmp.admin.config;

import cn.cuilan.ssmp.admin.annotation.IPArgumentResolver;
import cn.cuilan.ssmp.admin.annotation.LoginUserArgumentResolver;
import cn.cuilan.ssmp.exception.handler.BaseExceptionResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
public class MVCConfig implements WebMvcConfigurer {

    @Resource
    private IPArgumentResolver ipArgumentResolver;

    @Resource
    private LoginUserArgumentResolver loginUserArgumentResolver;

    /**
     * 统一输出编码
     */
    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    /**
     * 统一异常处理
     */
    @Bean
    public BaseExceptionResolver baseExceptionResolver() {
        return new BaseExceptionResolver();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 统一输出编码
        converters.add(responseBodyConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // IP注解解析器
        resolvers.add(ipArgumentResolver);
        // Logined注解解析器
        resolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        // 统一异常处理
        resolvers.add(baseExceptionResolver());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 设置默认页面
        registry.addViewController("/");
    }
}
