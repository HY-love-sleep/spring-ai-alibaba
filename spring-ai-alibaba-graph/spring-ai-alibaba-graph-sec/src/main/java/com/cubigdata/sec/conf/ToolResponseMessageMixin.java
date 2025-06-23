package com.cubigdata.sec.conf;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;

import java.util.List;
import java.util.Map;

public abstract class ToolResponseMessageMixin {

    @JsonCreator
    public ToolResponseMessageMixin(
            @JsonProperty("textContent") String textContent,
            @JsonProperty("metadata") Map<String, Object> metadata,
            @JsonProperty("responses") List<ToolResponse> responses) {
    }

    @JsonIgnore
    public abstract String getText();
}
