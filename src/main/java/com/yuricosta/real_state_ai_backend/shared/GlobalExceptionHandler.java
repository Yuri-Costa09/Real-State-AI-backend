package com.yuricosta.real_state_ai_backend.shared;

import com.yuricosta.real_state_ai_backend.shared.errors.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardError> notFoundException(NotFoundException e,
                                                           HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(
                java.time.Instant.now(),
                status.value(),
                "Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status.value()).body(err);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> badCredentialsException(BadCredentialsException e,
                                                                   HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardError err = new StandardError(
                java.time.Instant.now(),
                status.value(),
                "Bad Credentials",
                e.getMessage(),
                request.getRequestURI()

        );
        return ResponseEntity.status(status.value()).body(err);
    }
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<StandardError> badRequestException(HttpClientErrorException.BadRequest e,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(
                java.time.Instant.now(),
                status.value(),
                "Bad Request",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status.value()).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGeneric(Exception e,
                                                       HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardError err = new StandardError(
                java.time.Instant.now(),
                status.value(),
                "Internal Server Error",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
