//package com.yetcache.agent.broadcast.receiver;
//
//import com.yetcache.agent.broadcast.command.ExecutableCommand;
//import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandler;
//import com.yetcache.agent.broadcast.receiver.handler.CacheBroadcastHandlerRegistry;
//import lombok.extern.slf4j.Slf4j;
//import java.util.Optional;
//
///**
// * @author walter.yan
// * @since 2025/7/16
// */
//@Slf4j
//public class DefaultExecutableCommandDispatcher implements ExecutableCommandDispatcher {
//    private final CacheBroadcastHandlerRegistry handlerRegistry;
//
//    public DefaultExecutableCommandDispatcher(CacheBroadcastHandlerRegistry handlerRegistry) {
//        this.handlerRegistry = handlerRegistry;
//    }
//
//    @Override
//    public void onReceive(ExecutableCommand cmd) {
//        Optional<CacheBroadcastHandler> optional = handlerRegistry.getHandler(cmd);
//        if (optional.isPresent()) {
//            optional.get().handle(cmd);
//        } else {
//            log.error("[YetCache] No broadcast handler for: " + cmd);
//        }
//    }
//}
