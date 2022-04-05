package com.cutecatdog.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.cutecatdog.mapper")
public class MybatisCofig {
}
