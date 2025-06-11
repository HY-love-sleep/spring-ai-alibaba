package com.alibaba.cloud.ai.service.generator.workflow.sections;

import com.alibaba.cloud.ai.model.workflow.Node;
import com.alibaba.cloud.ai.model.workflow.NodeType;
import com.alibaba.cloud.ai.model.workflow.nodedata.CodeNodeData;
import com.alibaba.cloud.ai.service.generator.workflow.NodeSection;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CodeNodeSection implements NodeSection {

    @Override
    public boolean support(NodeType nodeType) {
        return NodeType.CODE.equals(nodeType);
    }

    @Override
    public String render(Node node, String varName) {
        CodeNodeData d = (CodeNodeData) node.getData();
        String id = node.getId();

        String codeLiteral = d.getCode()
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r\n", "\\n")
            .replace("\n", "\\n");

        String inputsList = d.getInputs().stream()
            .map(sel -> String.format("new VariableSelector(\"%s\",\"%s\")",
                sel.getNamespace(), sel.getName()))
            .collect(Collectors.joining(", "));
        String outputsList = d.getOutputs().stream()
            .map(v -> String.format("new Variable(\"%s\",\"%s\")",
                v.getName(), v.getValueType()))
            .collect(Collectors.joining(", "));

        return String.format(
            "        // —— CodeNode [%s] ——%n" +
            "        CodeNode %s = CodeNode.builder()%n" +
            "            .code(\"%s\")%n" +
            "            .codeLanguage(\"%s\")%n" +
            (inputsList.isEmpty()   ? "" : "            .inputs(List.of(" + inputsList + "))%n") +
            (outputsList.isEmpty()  ? "" : "            .outputs(List.of(" + outputsList + "))%n") +
            "            .build();%n" +
            "        stateGraph.addNode(\"%s\", AsyncNodeAction.node_async(%s));%n%n",
            id, varName,
            codeLiteral,
            d.getCodeLanguage(),
            id, varName
        );
    }

}
