package lab.anoper.yetcache.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lab.anoper.yetcache.example.domain.entity.StockHoldInfo;
import lab.anoper.yetcache.example.mapper.StockHoldInfoMapper;
import lab.anoper.yetcache.example.service.IStockHoldInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class StockHoldInfoServiceImpl extends ServiceImpl<StockHoldInfoMapper, StockHoldInfo>
        implements IStockHoldInfoService {
    @Override
    public List<StockHoldInfo> listByFundAccount(String fundAccount) {
        LambdaQueryWrapper<StockHoldInfo> queryWrapper = new LambdaQueryWrapper<StockHoldInfo>()
                .eq(StockHoldInfo::getFundAccount, fundAccount)
                .eq(StockHoldInfo::getDeleted, 0);
        return list(queryWrapper);
    }
}
