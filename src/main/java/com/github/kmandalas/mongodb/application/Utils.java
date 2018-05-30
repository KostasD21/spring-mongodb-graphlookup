package com.github.kmandalas.mongodb.application;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStreamReader;
import java.io.Reader;
import static java.nio.charset.StandardCharsets.UTF_8;


public class Utils {
    
    public static JsonNode readJson(String path) throws Exception {
    Reader reader = new InputStreamReader(new ClassPathResource(path).getInputStream(), UTF_8);
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, false);

    return mapper.readTree(reader);
  }
}
