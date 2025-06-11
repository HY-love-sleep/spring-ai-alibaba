package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.VariableSelector;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CodeNodeTest {

    @Test
    void builderAndGetters() {
        CodeNode node = CodeNode.builder()
            .code("print('hi')")
            .codeLanguage("python3")
            .inputs(List.of(new VariableSelector("", "foo")))
            .outputs(List.of("foo", "bar"))
            .build();

        assertEquals("print('hi')", node.getCode());
        assertEquals("python3", node.getCodeLanguage());
        assertEquals(1, node.getInputs().size());
        assertEquals(2, node.getOutputs().size());
    }

    @Test
    void applyCopiesInputsToOutputs() throws Exception {
        CodeNode node = CodeNode.builder()
            .inputs(List.of(new VariableSelector("", "in")))
            .outputs(List.of("in", "out"))
            .build();

        OverAllState state = new OverAllState().registerKeyAndStrategy("in", (o1, o2) -> o2);
        state.updateState(Map.of("in", "value1"));

        Map<String, Object> result = node.apply(state);

        assertEquals("value1", result.get("in"));
        assertEquals("value1", result.get("out"));  // 因为示例实现把同一输入拷贝到所有 outputs
    }
}
