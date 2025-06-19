package com.cubigdata.sec.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import lombok.extern.slf4j.Slf4j;


import java.util.Map;
import java.util.Set;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/18 15:58
 * todo:改为functionCall
 */
@Slf4j
public class SensitiveWordDecNode implements NodeAction {
    public static final String OUTPUT_KEY = "is_sensitive";

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Set<String> black = Set.of("苍井空", "中国民主党");
        return Map.of(OUTPUT_KEY, black.contains(state.value("field").orElse(""))?"yes":"no");
    }
}
