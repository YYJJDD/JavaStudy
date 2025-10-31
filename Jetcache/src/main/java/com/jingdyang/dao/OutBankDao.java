package com.jingdyang.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import com.jingdyang.consts.CacheNameConst;
import com.jingdyang.dao.mapper.OutBankMapper;
import com.jingdyang.entity.OutBankPositionDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
@Slf4j
public class OutBankDao {

    @Resource
    OutBankMapper outBankMapper;

    /**
     * 获取行外持仓
     *
     * @param uid 客户ID
     * @return 客户行外持仓
     */
    @Cached(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.OUT_BANK_POSITION,
            key = "#uid",
            localLimit = 100,
            expire = 3600,
            cacheType = CacheType.REMOTE)
    public OutBankPositionDO getOutBankPosition(String uid) {
        try {
            // 读mysql
            return outBankMapper.getOutBankPosition(uid);
        } catch (Exception e) {
            log.error("out bank position: get from mysql failed:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 保存行外持仓
     */
    @CacheUpdate(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.OUT_BANK_POSITION,
            key = "#outBankPositionDO.uid",
            value = "#outBankPositionDO")
    public void saveOutBankPosition(OutBankPositionDO outBankPositionDO) {
        try {
            // 写mysql
            outBankMapper.saveOutBankPosition(outBankPositionDO);
        } catch (Exception e) {
            log.error("out bank position: save to mysql failed:{}", e.getMessage());
        }
    }


}
