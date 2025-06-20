package com.cubigdata.sec.conf;

import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.KeyStrategy;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.node.ToolNode;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.cubigdata.sec.dispatcher.SensitiveDispatcher;
import com.cubigdata.sec.nodes.ClftNode;
import com.cubigdata.sec.nodes.SensitiveWordDecNode;
import com.cubigdata.sec.tools.FieldSaveTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncNodeAction.node_async;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 15:44
 */
@Configuration
@Slf4j
public class SecGraphBuilder {

    @Bean
    public StateGraph secGraph(ChatClient.Builder chatClientBuilder,
                               @Qualifier("sensitiveVectorStore") VectorStore sensitiveVectorStore,
                               @Qualifier("classificationVectorStore") VectorStore classificationVectorStore,
                               ChatMemory chatMemory,
                               FieldSaveTool toolBack,
                               ToolCallbackResolver toolCallbackResolver
    ) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> {
            HashMap<String, KeyStrategy> keyStrategyHashMap = new HashMap<>();
            keyStrategyHashMap.put("field", new ReplaceStrategy());
            keyStrategyHashMap.put("is_sensitive", new ReplaceStrategy());
            keyStrategyHashMap.put("clft_res", new ReplaceStrategy());
            keyStrategyHashMap.put("save_result", new ReplaceStrategy());
            return keyStrategyHashMap;
        };

        StateGraph stateGraph = new StateGraph(keyStrategyFactory);
        stateGraph.addEdge(START, "sensitive")
                .addNode("sensitive", node_async(new SensitiveWordDecNode()))
                .addNode("clft", node_async(new ClftNode(chatClientBuilder, classificationVectorStore, toolBack)))
                .addConditionalEdges("sensitive", AsyncEdgeAction.edge_async(new SensitiveDispatcher()), Map.of("yes", StateGraph.END, "no", "clft"))
                .addNode("saveTool", node_async(ToolNode.builder().llmResponseKey("clft_res")
                        .toolCallbacks(List.of(toolBack)).toolCallbackResolver(toolCallbackResolver).outputKey("save_result").build() ))
                .addEdge("clft", "saveTool")
                .addEdge("saveTool", StateGraph.END);

        // 添加 PlantUML 打印
        GraphRepresentation representation = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML,
                "sec flow");
        log.info("\n=== expander UML Flow ===");
        log.info(representation.content());
        log.info("==================================\n");

        return stateGraph;
    }


}
