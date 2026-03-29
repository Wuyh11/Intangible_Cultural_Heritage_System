package com.hubei.ich.heritage.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDetailVO {

    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Long regionId;
    private String regionName;
    private String level;
    private String batch;
    private String protectionUnit;
    private String coverImage;
    private String summary;
    private String content;
    private String longitude;
    private String latitude;
    private Integer status;
    private Integer viewCount;
    private Integer featured;
    private List<String> imageUrls;
    private List<InheritorVO> inheritors;
    private List<ProjectListVO> recommendations;
}
