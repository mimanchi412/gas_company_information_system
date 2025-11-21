package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    @Select({
            "<script>",
            "SELECT * FROM customers",
            "<where>",
            "<if test='kw != null and kw != \"\"'>",
            "(name LIKE CONCAT('%',#{kw},'%') OR address LIKE CONCAT('%',#{kw},'%') OR contact_phone LIKE CONCAT('%',#{kw},'%'))",
            "</if>",
            "</where>",
            "</script>"
    })
    List<Customer> search(@Param("kw") String keyword);
}
