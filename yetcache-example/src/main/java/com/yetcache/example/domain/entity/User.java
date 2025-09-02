package com.yetcache.example.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author walter.yan
 * @since 2025/6/28
 */
@TableName("user")
@Data
public class User {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String username;

    private String nickname;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    private Integer deleted;
}