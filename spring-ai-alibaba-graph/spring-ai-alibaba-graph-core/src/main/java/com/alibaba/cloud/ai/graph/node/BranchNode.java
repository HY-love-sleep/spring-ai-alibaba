package com.alibaba.cloud.ai.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.VariableSelector;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import javassist.compiler.ast.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BranchNode implements NodeAction {

    private final List<Case> cases;

    private BranchNode(List<Case> cases) {
        this.cases = cases;
    }

    private String outputKey;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Map<String, Object> out = new HashMap<>();
        for (Case c : cases) {
            boolean ok = c.getConditions().stream()
                .map(cond -> {
                    Object v = state.value(cond.getVariableSelector().getName()).orElse("");
                    return switch (cond.getComparisonOperator()) {
                        case "is" -> cond.getValue().equals(v);
                        default   -> false;
                    };
                })
                .allMatch(Predicate.isEqual(true));
            if (ok) {
                out.put("__next", c.getId());
                return out;
            }
        }
        out.put("__next", null);
        return out;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Case> cases;
        public Builder cases(List<Case> cases) {
            this.cases = cases;
            return this;
        }
        public BranchNode build() {
            return new BranchNode(this.cases);
        }
    }

    public static class Case {
        private String id;
        private List<Condition> conditions;
        public String getId() { return id; }
        public Case setId(String id) { this.id = id; return this; }
        public List<Condition> getConditions() { return conditions; }
        public Case setConditions(List<Condition> conds) { this.conditions = conds; return this; }

        public static class Condition {
            private VariableSelector variableSelector;
            private String comparisonOperator;
            private String value;
            public VariableSelector getVariableSelector() { return variableSelector; }
            public Condition setVariableSelector(VariableSelector sel) { this.variableSelector = sel; return this; }
            public String getComparisonOperator() { return comparisonOperator; }
            public Condition setComparisonOperator(String op) { this.comparisonOperator = op; return this; }
            public String getValue() { return value; }
            public Condition setValue(String value) { this.value = value; return this; }
        }
    }
}
