package com.cubigdata.sec.conf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistantMessageDeserializer extends JsonDeserializer<AssistantMessage> {

    @Override
    public AssistantMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = p.getCodec().readTree(p);

        String text = node.get("text").asText();

        // 解析 metadata
        Map<String, Object> metadata = new HashMap<>();
        JsonNode metadataNode = node.get("metadata");
        if (metadataNode != null && metadataNode.isObject()) {
            metadata = p.getCodec().treeToValue(metadataNode, Map.class);
        }

        // 解析 toolCalls
        List<ToolCall> toolCalls = new ArrayList<>();
        JsonNode toolCallsNode = node.get("toolCalls");
        if (toolCallsNode != null && toolCallsNode.isArray() && toolCallsNode.size() > 1) {
            JsonNode array = toolCallsNode.get(1);
            for (JsonNode item : array) {
                String id = item.get("id").asText();
                String type = item.get("type").asText();
                String name = item.get("name").asText();
                String arguments = item.get("arguments").asText();
                toolCalls.add(new ToolCall(id, type, name, arguments));
            }
        }

        return new AssistantMessage(text, metadata, toolCalls);
    }
}
