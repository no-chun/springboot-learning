package com.chun.json.util;

import com.chun.json.model.Info;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoDeserializer extends JsonDeserializer<Info> {
    @Override
    public Info deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String message = node.get("message").asText();
            Date time = format.parse(node.get("send_time").asText());
            return new Info(message, time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
