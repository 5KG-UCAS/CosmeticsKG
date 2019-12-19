package org.fivekg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.fivekg.Neo4j","org.fivekg.Controller"})


public class CosmeticskgApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CosmeticskgApplication.class, args);
    }

    // 不重写打包war部署到tomcat接口会报404
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CosmeticskgApplication.class);
    }
}
