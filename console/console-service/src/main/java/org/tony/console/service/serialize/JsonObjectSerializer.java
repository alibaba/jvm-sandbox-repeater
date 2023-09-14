package org.tony.console.service.serialize;

import com.alibaba.jvm.sandbox.repeater.plugin.core.serialize.SerializeException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.tony.console.service.utils.JacksonUtil;

import java.io.IOException;

/**
 * @author peng.hu1
 * @Date 2023/4/17 13:14
 */
public class JsonObjectSerializer extends JsonSerializer<JsonObject> {

    @Override
    public void serialize(JsonObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Gson gson = new Gson();
        String s = gson.toJson(value);
        gen.writeString(s);
    }
}
