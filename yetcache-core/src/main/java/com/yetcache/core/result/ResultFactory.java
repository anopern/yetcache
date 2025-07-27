package com.yetcache.core.result;

/**
 * @author walter.yan
 * @since 2025/7/26
 */
public class ResultFactory {

    public static <V> BaseSingleResult<V> successSingle(String componentName) {
        return new BaseSingleResult<>(componentName, CacheOutcome.SUCCESS, null, null, null);
    }

    public static <S, T> BaseBatchResult<S, T> successBatch(String componentName) {
        return new BaseBatchResult<>(componentName, CacheOutcome.SUCCESS, null, null, null);
    }

    public static <V> BaseResult<V> notFound(String componentName) {
        return BaseResult.notFound(componentName);
    }

    public static <V> BaseSingleResult<V> notFoundSingle(String componentName) {
        return new BaseSingleResult<>(componentName, CacheOutcome.NOT_FOUND, null, null, null);
    }

    public static <V> BaseResult<V> fail(String componentName, Throwable throwable) {
        return new BaseResult<>(componentName, CacheOutcome.FAIL, null, throwable);
    }

    public static BaseBatchResult<Void, Void> badParamBatch(String componentName) {
        return new BaseBatchResult<>(componentName, CacheOutcome.BAD_PARAM, null, null, null);
    }
}
