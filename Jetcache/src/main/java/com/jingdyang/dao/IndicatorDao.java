package com.jingdyang.dao;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.jingdyang.consts.CacheNameConst;
import com.jingdyang.dao.mapper.CharacterIndicatorDegradeMapper;
import com.jingdyang.dao.mapper.CharacterIndicatorMapper;
import com.jingdyang.entity.CharacterIndicatorDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class IndicatorDao {

    // mysql主库（和CharacterIndicatorDegradeMapper逻辑完全一致，对应不同数据源）
    @Resource
    CharacterIndicatorMapper characterIndicatorMapper;

    // mysql备库（和CharacterIndicatorMapper逻辑完全一致，对应不同数据源）
    @Resource
    CharacterIndicatorDegradeMapper characterIndicatorDegradeMapper;

    @SentinelResource(value = "readIndicatorInfo",
            fallback = "getCharacterIndicatorFallBack",
            blockHandler = "getCharacterIndicatorFallBack")
    @Cached(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.CHARACTER_INDICATOR,
            key = "#fundId",
            expire = 3700,
            localLimit = 1000,
            cacheType = CacheType.BOTH)
    public CharacterIndicatorDO getCharacterIndicator(String fundId) {
        return characterIndicatorMapper.getCharacterIndicator(fundId);
    }

    public CharacterIndicatorDO getCharacterIndicatorFallBack(String fundId) {
        return characterIndicatorDegradeMapper.getCharacterIndicator(fundId);
    }

}
