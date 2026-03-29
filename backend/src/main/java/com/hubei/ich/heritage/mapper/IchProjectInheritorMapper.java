package com.hubei.ich.heritage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hubei.ich.heritage.entity.IchProjectInheritor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IchProjectInheritorMapper extends BaseMapper<IchProjectInheritor> {

    @Select("SELECT inheritor_id FROM ich_project_inheritor WHERE project_id = #{projectId}")
    List<Long> findInheritorIdsByProjectId(Long projectId);

    @Select("SELECT project_id FROM ich_project_inheritor WHERE inheritor_id = #{inheritorId}")
    List<Long> findProjectIdsByInheritorId(Long inheritorId);
}
