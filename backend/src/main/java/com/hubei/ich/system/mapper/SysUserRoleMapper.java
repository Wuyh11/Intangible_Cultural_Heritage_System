package com.hubei.ich.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hubei.ich.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    @Select("""
            SELECT r.role_code
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId} AND r.deleted = 0
            """)
    List<String> findRoleCodesByUserId(Long userId);

    @Select("""
            SELECT ur.role_id
            FROM sys_user_role ur
            WHERE ur.user_id = #{userId}
            """)
    List<Long> findRoleIdsByUserId(Long userId);
}
