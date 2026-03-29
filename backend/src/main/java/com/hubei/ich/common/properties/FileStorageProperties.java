package com.hubei.ich.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage")
public class FileStorageProperties {

    private String uploadDir = "./uploads";
    private String accessPrefix = "/uploads/";
}
