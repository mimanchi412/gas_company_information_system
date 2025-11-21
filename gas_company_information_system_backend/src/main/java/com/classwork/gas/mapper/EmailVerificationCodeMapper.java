package com.classwork.gas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classwork.gas.entity.EmailVerificationCode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailVerificationCodeMapper extends BaseMapper<EmailVerificationCode> {
}
