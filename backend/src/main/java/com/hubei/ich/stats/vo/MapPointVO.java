package com.hubei.ich.stats.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MapPointVO {

    private Long projectId;
    private String projectName;
    private Long categoryId;
    private String categoryName;
    private Long regionId;
    private String regionName;
    private String level;
    private String longitude;
    private String latitude;
    private Integer viewCount;
}
