package com.alibaba.repeater.console.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 *
 * @author zhaoyb1990
 */
@SpringBootApplication
@MapperScan("com.alibaba.repeater.console.dal.mapper")
@ComponentScan("com.alibaba.repeater.console")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
