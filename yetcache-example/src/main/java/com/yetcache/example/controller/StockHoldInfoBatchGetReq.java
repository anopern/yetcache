package com.yetcache.example.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/7
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockHoldInfoBatchGetReq {
    private String fundAccount;
    private List<Long> ids;
}
