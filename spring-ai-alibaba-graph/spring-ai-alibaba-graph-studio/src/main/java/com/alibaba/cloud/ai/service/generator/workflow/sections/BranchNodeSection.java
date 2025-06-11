/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.ai.service.generator.workflow.sections;

import com.alibaba.cloud.ai.model.workflow.Node;
import com.alibaba.cloud.ai.model.workflow.NodeType;
import com.alibaba.cloud.ai.model.workflow.nodedata.BranchNodeData;
import com.alibaba.cloud.ai.service.generator.workflow.NodeSection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BranchNodeSection implements NodeSection {

    @Override
    public boolean support(NodeType nodeType) {
        return NodeType.BRANCH.equals(nodeType);
    }

    @Override
    public String render(Node node, String varName) {
        BranchNodeData d = (BranchNodeData) node.getData();
        String id = node.getId();

        String casesLiteral = d.getCases().stream()
            .map(c -> String.format("new Case().setId(\"%s\").setLogicalOperator(\"%s\").setConditions(List.of(%s))",
                c.getCaseId(),
                c.getLogicalOperator(),
                c.getConditions().stream()
                    .map(cond -> String.format("new Case.Condition()" +
                            ".setVariableSelector(new VariableSelector(\"%s\",\"%s\"))" +
                            ".setComparisonOperator(\"%s\")" +
                            ".setValue(\"%s\")",
                        cond.getVariableSelector().getNamespace(),
                        cond.getVariableSelector().getName(),
                        cond.getComparisonOperator(),
                        cond.getValue()
                    ))
                    .collect(Collectors.joining(", "))
            ))
            .collect(Collectors.joining(",\n                "));

        return String.format(
            "        // —— BranchNode [%s] ——%n" +
            "        BranchNode %s = BranchNode.builder()%n" +
            "            .cases(List.of(%s))%n" +
            "            .build();%n" +
            "        stateGraph.addNode(\"%s\", AsyncNodeAction.node_async(%s));%n%n",
            id, varName, casesLiteral, id, varName
        );
    }

}
