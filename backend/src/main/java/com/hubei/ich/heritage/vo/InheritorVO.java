package com.hubei.ich.heritage.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InheritorVO {

    private Long id;
    private String name;
    private String gender;
    private Integer birthYear;
    private Long regionId;
    private String regionName;
    private String title;
    private String avatar;
    private String introduction;
    private String representativeWorks;
    private Integer status;
    private List<Long> projectIds;
}
