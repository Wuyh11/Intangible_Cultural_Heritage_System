package com.hubei.ich.heritage.service;

import com.hubei.ich.common.result.PageResponse;
import com.hubei.ich.heritage.dto.InheritorQueryDTO;
import com.hubei.ich.heritage.dto.InheritorSaveDTO;
import com.hubei.ich.heritage.vo.InheritorVO;

import java.util.List;

public interface InheritorService {

    PageResponse<InheritorVO> pageInheritors(InheritorQueryDTO dto);

    InheritorVO saveInheritor(InheritorSaveDTO dto);

    void deleteInheritor(Long id);

    List<InheritorVO> listByProjectId(Long projectId);
}
