package com.yetcache.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author walter.yan
 * @since 2025/8/29
 */
@Data
public class QuoteSimpleQuoteList {
    @JsonProperty("list")
    private List<QuoteSimpleQuoteRespVO> list;
}