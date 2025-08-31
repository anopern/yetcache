package com.yetcache.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;
    private Integer deleted;
    @Transient
    @TableField(exist = false)
    private Boolean forceRefresh;
}
