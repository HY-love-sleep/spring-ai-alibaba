package com.cubigdata.sec.conf;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.ai.chat.messages.ToolResponseMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolResponseMessageDeserializer extends JsonDeserializer<ToolResponseMessage> {

    @Override
    public ToolResponseMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonNode node = p.getCodec().readTree(p);

        // 解析 responses
        List<ToolResponseMessage.ToolResponse> responses = new ArrayList<>();
        JsonNode responsesNode = node.get("responses");
        if (responsesNode != null && responsesNode.isArray() && responsesNode.size() > 1) {
            JsonNode array = responsesNode.get(1); // 跳过["java.util.ArrayList", [...]]
            for (JsonNode item : array) {
                String id = item.get("id").asText();
                String name = item.get("name").asText();
                String responseData = item.get("responseData").asText();
                responses.add(new ToolResponseMessage.ToolResponse(id, name, responseData));
            }
        }

        // 解析 metadata
        Map<String, Object> metadata = new HashMap<>();
        JsonNode metadataNode = node.get("metadata");
        if (metadataNode != null && metadataNode.isObject()) {
            metadata = p.getCodec().treeToValue(metadataNode, Map.class);
        }

        return new ToolResponseMessage(responses, metadata);
    }
}
