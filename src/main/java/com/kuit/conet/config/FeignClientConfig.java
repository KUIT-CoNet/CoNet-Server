package com.kuit.conet.config;

import com.kuit.conet.ConetApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = ConetApplication.class)
public class FeignClientConfig {
}

/**
 * Apple 서버와 통신
 * */