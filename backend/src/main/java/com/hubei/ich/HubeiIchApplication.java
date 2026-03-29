package com.hubei.ich;

import com.hubei.ich.common.properties.FileStorageProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.hubei.ich.**.mapper")
@EnableConfigurationProperties(FileStorageProperties.class)
public class HubeiIchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HubeiIchApplication.class, args);
    }
}
