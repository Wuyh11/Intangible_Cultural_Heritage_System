package com.hubei.ich.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AdminUserSaveDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String email;
    private String phone;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotEmpty(message = "请至少选择一个角色")
    private List<String> roleCodes;
}
