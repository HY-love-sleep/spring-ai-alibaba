package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.VariableSelector;
import com.alibaba.cloud.ai.graph.action.NodeAction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//todo: this is a mock node for dsl, need refer to the CoderNoder implementation
public class CodeNode implements NodeAction {

    private final String code;
    private final String codeLanguage;
    private final List<VariableSelector> inputs;
    private final List<String> outputs;

    private CodeNode(String code, String codeLanguage,
                     List<VariableSelector> inputs, List<String> outputs) {
        this.code = code;
        this.codeLanguage = codeLanguage;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // this simply copies all input variables to each output as is
        Map<String, Object> result = new HashMap<>();
        String firstKey = inputs.isEmpty() ? null : inputs.get(0).getName();
        Object value0 = firstKey == null ? "" : state.value(firstKey).orElse("");
        for (String outKey : this.outputs) {
            result.put(outKey, value0);
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private String codeLanguage;
        private List<VariableSelector> inputs = List.of();
        private List<String> outputs = List.of();

        public Builder code(String code) {
            this.code = code;
            return this;
        }
        public Builder codeLanguage(String lang) {
            this.codeLanguage = lang;
            return this;
        }
        public Builder inputs(List<VariableSelector> inputs) {
            this.inputs = inputs;
            return this;
        }
        public Builder outputs(List<String> outputs) {
            this.outputs = outputs;
            return this;
        }
        public CodeNode build() {
            return new CodeNode(this.code, this.codeLanguage, this.inputs, this.outputs);
        }
    }

    public String getCode() { return code; }
    public String getCodeLanguage() { return codeLanguage; }
    public List<VariableSelector> getInputs() { return inputs; }
    public List<String> getOutputs() { return outputs; }
}
