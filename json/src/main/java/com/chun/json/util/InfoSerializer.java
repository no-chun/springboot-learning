package com.chun.json.util;

import com.chun.json.model.Info;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class InfoSerializer extends JsonSerializer<Info> {
    @Override
    public void serialize(Info info, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("message", info.getMsg());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonGenerator.writeStringField("send_time", dateFormat.format(info.getTime()));
        jsonGenerator.writeEndObject();
    }
}
