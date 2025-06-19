package com.cubigdata.sec.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/19 17:20
 */
public class ClftNode implements NodeAction {
    public static final String OUTPUT_KEY = "clft_res";
    private final ChatClient chatClient;

    public ClftNode(ChatClient.Builder modelBuilder,
                    @Qualifier("classificationVectorStore") VectorStore classificationVectorStore) {
        this.chatClient = modelBuilder
                .defaultSystem("""
							你是一个数据安全分类分级助手，请根据用户输入的字段名和下方提供的字段分类知识，判断该字段属于哪个分类路径，分级是多少，并简要说明理由。
							请使用如下格式输出一个json：
								 字段名：...
								 分类路径：一级 > 二级 > 三级 > 四级
								 分级：第X级
								 理由：...
						""")
                .defaultAdvisors(
//                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor
                                .builder(classificationVectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(5)
                                        .similarityThresholdAll()
                                        .build())
                                .build(),
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String field = (String) state.value("field").orElse("");
        String res = chatClient.prompt()
                .user(field)
                .call()
                .content();
        return Map.of(OUTPUT_KEY, res);
    }

}
