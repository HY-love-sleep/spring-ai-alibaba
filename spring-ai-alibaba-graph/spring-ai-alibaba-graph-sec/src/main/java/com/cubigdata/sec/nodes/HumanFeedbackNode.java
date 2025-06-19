package com.cubigdata.sec.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.RunnableErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yHong
 * @since 2025/6/19 11:07
 * @version 1.0
 * 人类反馈
 */
public class HumanFeedbackNode implements NodeAction {
    private static final Logger logger = LoggerFactory.getLogger(HumanFeedbackNode.class);

    @Override
    public Map<String, Object> apply(OverAllState state) throws GraphRunnerException {
        if (state.humanFeedback() == null || !state.isResume()) {
            throw RunnableErrors.subGraphInterrupt.exception("interrupt");
        }

        logger.info("human_feedback node is running.");
        HashMap<String, Object> resultMap = new HashMap<>();
        String nextStep = StateGraph.END;

        Map<String, Object> feedBackData = state.humanFeedback().data();
        boolean feedback = (boolean) feedBackData.getOrDefault("feed_back", true);
        if (feedback) {
            nextStep = "translate";
        }

        resultMap.put("human_next_node", nextStep);
        logger.info("human_feedback node -> {} node", nextStep);
        return resultMap;
    }
}
