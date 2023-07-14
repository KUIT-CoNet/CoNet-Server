package com.kuit.conet.config;

import com.kuit.conet.utils.BearerAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private final ClientIpResolver clientIpResolver;
    private final BearerAuthInterceptor bearerAuthInterceptor;

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(clientIpResolver);
    }

    public void addInterceptors(InterceptorRegistry registry){
        log.info("Interceptor 등록");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/auth/regenerate-token");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/auth/term-and-name");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/create");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/participate");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/user/delete");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/leave");
    }
}