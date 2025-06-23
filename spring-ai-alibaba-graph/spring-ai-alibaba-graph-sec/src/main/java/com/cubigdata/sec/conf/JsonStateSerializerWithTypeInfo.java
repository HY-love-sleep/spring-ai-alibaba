package com.cubigdata.sec.conf;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.serializer.plain_text.PlainTextStateSerializer;
import com.alibaba.cloud.ai.graph.state.AgentStateFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

public class JsonStateSerializerWithTypeInfo extends PlainTextStateSerializer {

    private final ObjectMapper mapper;

    public JsonStateSerializerWithTypeInfo(AgentStateFactory<OverAllState> stateFactory) {
        super(stateFactory);
        this.mapper = new ObjectMapper();

        this.mapper.addMixIn(AssistantMessage.class, AssistantMessageMixin.class);
        this.mapper.addMixIn(ToolResponseMessage.class, ToolResponseMessageMixin.class);

        this.mapper.activateDefaultTyping(
                this.mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String serialize(OverAllState state) throws IOException {
        return mapper.writeValueAsString(state.data());
    }

    public OverAllState deserialize(String data) throws IOException {
        Map<String, Object> rawMap = mapper.readValue(data, new TypeReference<>() {});
        return stateFactory().apply(rawMap);
    }

    @Override
    public OverAllState cloneObject(OverAllState state) throws IOException {
        String json = serialize(state);
        return deserialize(json);
    }

    @Override
    public void write(OverAllState obj, ObjectOutput out) throws IOException {
        String json = serialize(obj);
        out.writeUTF(json);
    }

    @Override
    public OverAllState read(ObjectInput in) throws IOException {
        String json = in.readUTF();
        return deserialize(json);
    }
}
