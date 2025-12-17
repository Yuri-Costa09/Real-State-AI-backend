package com.yuricosta.real_state_ai_backend.properties.ai;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;
import com.yuricosta.real_state_ai_backend.properties.dtos.PropertyFilter;
import org.springframework.stereotype.Service;

@Service
public class SemanticSearchService {
    private final Client client;
    private final SemanticSearchParser semanticSearchParser;

    public SemanticSearchService(Client client, SemanticSearchParser semanticSearchParser) {
        this.client = client;
        this.semanticSearchParser = semanticSearchParser;
    }

    public PropertyFilter performSemanticSearch(String text) throws Exception {

        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(
                        Content.fromParts(Part.fromText(GeminiPrompt.SEMANTIC_SEARCH_PROMPT)))
                .build();

        var response = client.models.generateContent(
                "gemini-2.5-flash",
                text,
                config
        );

        return semanticSearchParser.parse(response);
    }

}
