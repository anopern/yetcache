package com.yetcache.core.support.key;

import cn.hutool.core.util.StrUtil;
import com.yetcache.core.config.TenantMode;
import com.yetcache.core.support.tenant.TenantProvider;
import lombok.Data;

/**
 * @author walter.yan
 * @since 2025/7/1
 */
@Data
public abstract class AbstractKeyConverter<K> implements KeyConverter<K> {

}
