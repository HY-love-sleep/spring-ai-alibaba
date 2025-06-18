package com.cubigdata.sec.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Map;

import static org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor.TOP_K;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @author yHong
 * @version 1.0
 * @since 2025/6/18 15:58
 */
public class SensitiveWordDecNode implements NodeAction {
    public static final String OUTPUT_KEY = "is_sensitive";
    private final ChatClient chatClient;

    public SensitiveWordDecNode(ChatClient.Builder modelBuilder,
                                VectorStore classificationVectorStore,
                                ChatMemory chatMemory) {
        this.chatClient = modelBuilder
                .defaultSystem("""
							你是一个敏感词检测助手， 请你判断用户的输入是否包含敏感词, 输出 yes 或者 no
						""")
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
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
        String fieldName = (String) state.value("fieldName").orElse("");
        String chatId = (String) state.value("chat_id").orElse("");
        String res = chatClient.prompt()
                .user(fieldName)
                .advisors(a -> a.param(CONVERSATION_ID, chatId).param(TOP_K, 100)) // 设置advisor参数， 记忆使用chatId， 拉取最近的100条记录
                .call()
                .content();
        return Map.of(OUTPUT_KEY, res);
    }
}
