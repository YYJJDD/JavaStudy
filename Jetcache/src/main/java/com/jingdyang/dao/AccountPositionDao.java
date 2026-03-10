package com.jingdyang.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.jingdyang.consts.CacheNameConst;
import com.jingdyang.entity.CardInfoBO;
import com.jingdyang.entity.UserFundInfoBO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AccountPositionDao {

    /**
     * 请求续传接口
     */
    @Cached(area = CacheNameConst.JETCACHE_AREA_NAME,
            name = CacheNameConst.INTERNAL_POSITION,
            key = "#uid",
            localLimit = 500,
            expire = 600,
            cacheType = CacheType.REMOTE)
    public List<UserFundInfoBO> getInternalPositionInfo(String uid,
                                                        List<CardInfoBO> cardList) {
        List<UserFundInfoBO> userFundInfoBOList = new ArrayList<>();

        // 递归请求上游续传接口

        // 如果查询结果为空列表则返回null，避免结果被缓存
        return userFundInfoBOList.isEmpty() ? null : userFundInfoBOList;

    }

}
