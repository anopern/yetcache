package lab.anoper.yetcache.example.domain.entity;

import lombok.Data;

import java.util.List;

@Data
public class AccAccountInfo {
    private Long id;
    private String uuid;
    private List<String> fundAccounts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getFundAccounts() {
        return fundAccounts;
    }

    public void setFundAccounts(List<String> fundAccounts) {
        this.fundAccounts = fundAccounts;
    }
}
