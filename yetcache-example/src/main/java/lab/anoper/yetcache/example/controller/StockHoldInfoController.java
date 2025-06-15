package lab.anoper.yetcache.example.controller;

import lab.anoper.yetcache.example.base.common.cache.agent.StockHoldInfoAgentMulti;
import lab.anoper.yetcache.example.base.common.cache.dto.StockHoldInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/stockHoldInfos")
public class StockHoldInfoController {
    @Autowired
    private StockHoldInfoAgentMulti agent;

    @RequestMapping("/listByFundAccount")
    public List<StockHoldInfoDTO> listByFundAccount(@RequestParam String fundAccount) {
        return agent.list(fundAccount);
    }
}
