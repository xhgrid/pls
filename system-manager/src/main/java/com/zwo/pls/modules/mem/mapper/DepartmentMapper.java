package com.zwo.pls.modules.mem.mapper;

import com.zwo.pls.modules.mem.domain.Department;
import com.zwo.pls.modules.mem.domain.DepartmentCriteria;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DepartmentMapper extends Mapper<Department> {
    long countByExample(DepartmentCriteria example);

    int deleteByExample(DepartmentCriteria example);

    List<Department> selectByExample(DepartmentCriteria example);

    int updateByExampleSelective(@Param("record") Department record, @Param("example") DepartmentCriteria example);

    int updateByExample(@Param("record") Department record, @Param("example") DepartmentCriteria example);
}