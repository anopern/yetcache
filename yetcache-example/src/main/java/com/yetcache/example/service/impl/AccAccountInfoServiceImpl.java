package com.yetcache.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yetcache.example.domain.entity.AccAccountInfo;
import com.yetcache.example.mapper.AccAccountInfoMapper;
import com.yetcache.example.service.IAccAccountInfoService;
import com.yetcache.utils.TransactionalEventUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AccAccountInfoServiceImpl extends ServiceImpl<AccAccountInfoMapper, AccAccountInfo>
        implements IAccAccountInfoService {
    @Autowired
    private TransactionalEventUtils transactionalEventUtils;

    @Transactional
    public boolean updateById(AccAccountInfo entity) {
        super.updateById(entity);
        transactionalEventUtils.publishAfterCommit(entity);
        return true;
    }
}
