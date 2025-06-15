package lab.anoper.yetcache.example.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class AccAccountInfo {
    private Long id;
    private String uuid;
    private List<String> fundAccounts;

    //其他字段
}
