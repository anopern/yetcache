package com.yetcache.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionalEventUtils {
    @Autowired
    private ApplicationEventPublisher publisher;

    public void publishAfterCommit(Object event) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                publisher.publishEvent(event);
            }
        });
    }
}
