package com.cubigdata.sec.conf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.content.Media;

import java.util.List;
import java.util.Map;

public abstract class AssistantMessageMixin {

    @JsonCreator
    public AssistantMessageMixin(
            @JsonProperty("textContent") String textContent,
            @JsonProperty("metadata") Map<String, Object> metadata,
            @JsonProperty("toolCalls") List<AssistantMessage.ToolCall> toolCalls,
            @JsonProperty("media") List<Media> media) {
        // 无需实现，供Jackson参考
    }

    @JsonProperty("textContent")
    public abstract String getTextContent();

    @JsonProperty("metadata")
    public abstract Map<String, Object> getMetadata();

    @JsonProperty("toolCalls")
    public abstract List<AssistantMessage.ToolCall> getToolCalls();

    @JsonProperty("media")
    public abstract List<Media> getMedia();

    @JsonIgnore
    public abstract String getText();
}
