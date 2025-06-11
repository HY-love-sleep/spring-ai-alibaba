// src/test/java/com/alibaba/cloud/ai/graph/node/BranchNodeTest.java
package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.VariableSelector;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BranchNodeTest {

    @Test
    void happyPathTrue() throws Exception {
        BranchNode.Case.Condition cond = new BranchNode.Case.Condition()
            .setVariableSelector(new VariableSelector("", "flag"))
            .setComparisonOperator("is")
            .setValue("yes");
        BranchNode.Case case1 = new BranchNode.Case().setId("caseTrue").setConditions(List.of(cond));
        BranchNode node = BranchNode.builder().cases(List.of(case1)).build();

        OverAllState state = new OverAllState().registerKeyAndStrategy("flag", (o1,o2)->o2);
        state.updateState(Map.of("flag", "yes"));

        Map<String,Object> out = node.apply(state);
        assertEquals("caseTrue", out.get("__next"));
    }

    @Test
    void noMatch() throws Exception {
        BranchNode.Case.Condition cond = new BranchNode.Case.Condition()
            .setVariableSelector(new VariableSelector("", "x"))
            .setComparisonOperator("is")
            .setValue("foo");
        BranchNode.Case case1 = new BranchNode.Case().setId("caseA").setConditions(List.of(cond));
        BranchNode node = BranchNode.builder().cases(List.of(case1)).build();

        OverAllState state = new OverAllState().registerKeyAndStrategy("x", (o1,o2)->o2);
        state.updateState(Map.of("x", "bar"));

        Map<String,Object> out = node.apply(state);
        assertNull(out.get("__next"));
    }
}
