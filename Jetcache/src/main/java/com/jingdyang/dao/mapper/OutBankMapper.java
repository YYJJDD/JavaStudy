package com.jingdyang.dao.mapper;

import com.jingdyang.entity.OutBankPositionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OutBankMapper {

    @Select("select * from xx where uid = #{uid}")
    OutBankPositionDO getOutBankPosition(String uid);

    @Update("")
    void saveOutBankPosition(OutBankPositionDO outBankPositionDO);

}
