package com.cubigdata.sec.dispatcher;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 11:21
 */
public class HumanFeedbackDispatcher implements EdgeAction {
    @Override
    public String apply(OverAllState state) throws Exception {
        return (String) state.value("human_next_node", StateGraph.END);
    }
}
