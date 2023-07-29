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
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/user");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/user/name");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/user/image");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/user/delete");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/create");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/participate");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/leave");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/bookmark");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/bookmark/delete");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/plan/time");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/plan/user-time");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/home/month");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/home/day");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/home/waiting");
        registry.addInterceptor(bearerAuthInterceptor).addPathPatterns("/team/detail");
    }
}