package com.yuricosta.real_state_ai_backend.shared;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*

       Aqui você pode adicionar métodos para tratar exceções globais
       usando @ExceptionHandler, retornando respostas padronizadas
       usando a classe ApiResponse.


       Exemplo:

       @ExceptionHandler(NOMEDAEXCEPTION.class)
       public ResponseEntity<StandardError> NOMEDAEXCEPTION(NOMEDAEXEPTION e,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // CODIGO DE STATUS HTTP
        StandardError err = new StandardError(
                java.time.Instant.now(),
                status.value(),
                "MENSAGEM DE ERRO",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    */

}
