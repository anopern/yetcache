package lab.anoper.yetcache.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lab.anoper.yetcache.example.domain.entity.AccAccountInfo;
import lab.anoper.yetcache.example.mapper.AccAccountInfoMapper;
import lab.anoper.yetcache.example.service.IAccAccountInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class AccAccountInfoServiceImpl extends ServiceImpl<AccAccountInfoMapper, AccAccountInfo>
        implements IAccAccountInfoService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public boolean updateById(AccAccountInfo entity) {
        super.updateById(entity);
        eventPublisher.publishEvent(entity);
        return true;
    }
}
