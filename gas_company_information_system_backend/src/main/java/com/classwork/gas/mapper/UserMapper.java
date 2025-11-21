package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
