package com.yuricosta.real_state_ai_backend.properties.ai;

import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// criar bean do client do gemini
@Configuration
public class GeminiConfig {

    @Bean
    public Client geminiClient() {
        return new Client();
    }
}
