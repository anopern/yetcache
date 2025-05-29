//package lab.anoper.yetcache.tenant;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author walter.yan
// * @since 2025/5/24
// */
//@Slf4j
//@Component
//public class DefaultTenantIdProvider implements ITenantIdProvider {
//
//    @Override
//    public Set<Long> listAllTenantIds() {
//        Set<Long> set = new HashSet<>();
//        set.add(TenantConstant.DEFAULT_TENANT_ID);
//        set.add(1001L);
//        set.add(1002L);
//        return set;
//    }
//}
