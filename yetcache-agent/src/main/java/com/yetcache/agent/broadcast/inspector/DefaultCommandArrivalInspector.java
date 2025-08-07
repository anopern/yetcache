//package com.yetcache.agent.broadcast.inspector;
//
//import com.yetcache.agent.broadcast.command.ExecutableCommand;
//import com.yetcache.core.config.broadcast.BroadcastDelayTolerance;
//import lombok.AllArgsConstructor;
//
///**
// * @author walter.yan
// * @since 2025/7/28
// */
//@AllArgsConstructor
//public class DefaultCommandArrivalInspector implements CommandArrivalInspector {
//    private final BroadcastDelayTolerance delayTolerance;
//
//    @Override
//    public boolean isTooLate(ExecutableCommand cmd) {
//        return cmd.getCreatedTime() + delayTolerance.getMaxDelaySecs() * 1000 > System.currentTimeMillis();
//    }
//}
