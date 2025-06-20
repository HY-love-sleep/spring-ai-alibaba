package com.cubigdata.sec.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.cubigdata.sec.tools.FieldSaveTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Map;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 17:20
 */
public class ClftNode implements NodeAction {
    public static final String OUTPUT_KEY = "clft_res";
    private final ChatClient chatClient;

    public ClftNode(ChatClient.Builder modelBuilder, @Qualifier("classificationVectorStore") VectorStore classificationVectorStore, FieldSaveTool fieldSaveTool) {
        this.chatClient = modelBuilder.defaultSystem("""
                        	你是一个数据安全分类分级助手，请根据用户输入的字段名，结合下方提供的字段分类知识，判断该字段属于哪个分类路径，分级是多少，并简要说明理由。
                        
                           然后请调用工具 `save_field_classification` 并传入如下 JSON 格式的参数：
                        
                           ```json
                           {
                             "fieldName": "xxx",
                             "classification": "一级 > 二级 > 三级",
                             "level": 3,
                             "reasoning": "你的解释理由"
                           }
                        """).defaultToolNames("save_field_classification")
                .defaultOptions(ToolCallingChatOptions.builder().toolCallbacks(List.of(fieldSaveTool)).internalToolExecutionEnabled(false).build())

                .defaultAdvisors(
//                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(classificationVectorStore).searchRequest(SearchRequest.builder().topK(5).similarityThresholdAll().build()).build(), new SimpleLoggerAdvisor()).build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String fieldName = (String) state.value("field").orElseThrow();

        AssistantMessage assistantMessage = chatClient.prompt().user(fieldName).call().chatResponse().getResult().getOutput();

        return Map.of(OUTPUT_KEY, assistantMessage);
    }

}
