package lab.anoper.yetcache.example.assember;

import lab.anoper.yetcache.example.base.common.cache.dto.StockHoldInfoDTO;
import lab.anoper.yetcache.example.domain.entity.StockHoldInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface StockHoldInfoConverter {
    StockHoldInfoConverter INSTANCE = Mappers.getMapper(StockHoldInfoConverter.class);

    List<StockHoldInfoDTO> doListToDTOList(List<StockHoldInfo> list);
}
