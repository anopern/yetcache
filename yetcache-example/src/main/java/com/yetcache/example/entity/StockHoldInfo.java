package com.yetcache.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@TableName("stock_hold_info")
@Data
public class StockHoldInfo {
    @Id
    private Long id;
    private Long userId;
    private String fundAccount;
    private String exchangeType;
    private String code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    private Integer deleted;
    @Transient
    private Boolean forceRefresh;
}
