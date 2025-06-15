package lab.anoper.yetcache.example.event;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lab.anoper.yetcache.example.domain.entity.AccAccountInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccAccountUpdateEventHandler {
    @Autowired
    private AccAccountInfoIdKeyCacheAgent accAccountInfoIdKeyCacheAgent;
    @Autowired
    private AccAccountInfoUuidKeyCacheAgent accAccountInfoUuidKeyCacheAgent;
    @Autowired
    private AccAccountInfoFundAccountKeyCacheAgent accAccountInfoFundAccountKeyCacheAgent;

    @EventListener
    public void onUpdateEvent(AccAccountInfo acc) {
        // 所有需要的操作
    }

    private void refreshAccAccountInfoCacheAgents(AccAccountInfo acc) {
        if (null == acc.getId()) {
            log.error("刷新AccAccountInfo的以Id为key的缓存失败，原因：事件中Id为空，请检查缓存一致性！event: {}", JSON.toJSON(acc));
        } else {
            accAccountInfoIdKeyCacheAgent.refreshCache(String.valueOf(acc.getId()));
        }
        if (StrUtil.isBlank(acc.getUuid())) {
            log.error("刷新AccAccountInfo的以Uuid为key的缓存失败，原因：事件中Uuid为空，请检查缓存一致性！event: {}", JSON.toJSON(acc));
        } else {
            accAccountInfoUuidKeyCacheAgent.refreshCache(acc.getUuid());
        }
        if (CollUtil.isEmpty(acc.getFundAccounts())) {
            log.error("刷新AccAccountInfo的以FundAccount为key的缓存失败，原因：事件中FundAccount为空，请检查缓存一致性！event: {}", JSON.toJSON(acc));
        } else {
            acc.getFundAccounts().forEach(fundAccount -> accAccountInfoFundAccountKeyCacheAgent.refreshCache(fundAccount));
        }
    }
}
