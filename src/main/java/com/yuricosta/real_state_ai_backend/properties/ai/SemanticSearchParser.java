package com.yuricosta.real_state_ai_backend.properties.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentResponse;
import com.yuricosta.real_state_ai_backend.properties.dtos.PropertyFilter;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchParser {
    private final ObjectMapper objectMapper;

    public SemanticSearchParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public PropertyFilter parse(GenerateContentResponse response) throws JsonProcessingException {
        String raw = response.text();
        String cleanJson = extractJson(raw);

        return objectMapper.readValue(cleanJson, PropertyFilter.class);
    }

    private String extractJson(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Empty AI response");
        }

        // remove ```json and ```
        return text
                .replaceAll("(?s)```json", "")
                .replaceAll("(?s)```", "")
                .trim();
    }
}
