package com.cubigdata.sec.dispatcher;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 17:37
 */
public class SensitiveDispatcher implements EdgeAction {
    @Override
    public String apply(OverAllState state) throws Exception {
        return (String) state.value("is_sensitive").orElse("no");
    }
}
