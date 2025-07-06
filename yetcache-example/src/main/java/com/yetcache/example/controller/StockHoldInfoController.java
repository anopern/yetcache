//package com.yetcache.example.controller;
//
//import com.yetcache.example.entity.StockHoldInfo;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author walter.yan
// * @since 2025/6/28
// */
//@RestController
//@RequestMapping("/api/stock-hold-infos")
//@Slf4j
//@AllArgsConstructor
//public class StockHoldInfoController {
//    private final StockHoldInfoCacheAgent agent;
//
//    @PostMapping("/getById")
//    public StockHoldInfo getById(@RequestBody StockHoldInfo dto) {
//        return agent.getById(dto.getFundAccount(), dto.getId());
//    }
//}
