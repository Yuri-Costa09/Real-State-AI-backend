package com.yuricosta.real_state_ai_backend.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // Construtor privado para forçar o uso dos métodos estáticos (Factory Method Pattern)
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 1. Sucesso passando dados e mensagem.
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    // 2. Sucesso passando apenas dados. | Mensagem padrão.
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operação realizada com sucesso", data);
    }

    // 2. Sucesso passando apenas mensagem. | Sem dados.
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }
}
