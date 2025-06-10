package lab.anoper.yetcache.enums;

public enum CacheEventType {
    UPDATE,              // KV 更新
    INVALIDATE,          // KV 删除
    UPDATE_ENTRY,        // Hash field 更新
    INVALIDATE_FIELD,    // Hash field 删除
    UPDATE_ALL_FIELDS,
    INVALIDATE_ALL_FIELDS
}
