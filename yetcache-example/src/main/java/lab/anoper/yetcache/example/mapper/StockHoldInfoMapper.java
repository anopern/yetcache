package lab.anoper.yetcache.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lab.anoper.yetcache.example.domain.entity.StockHoldInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockHoldInfoMapper extends BaseMapper<StockHoldInfo> {
}
