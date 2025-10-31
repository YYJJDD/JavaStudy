package com.jingdyang.dao.mapper;

import com.jingdyang.entity.CharacterIndicatorDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CharacterIndicatorDegradeMapper {

    @Select("select * from xx_table_degrade where fundId = #{fundId}")
    public CharacterIndicatorDO getCharacterIndicator(String fundId);

}
