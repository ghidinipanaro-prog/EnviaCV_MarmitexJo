package com.enviacv.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Campos obrigatórios em branco ou inválidos -> 400
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> campoInvalido(BindException e) {
        List<String> erros = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return resposta(HttpStatus.BAD_REQUEST, erros);
    }

    // PDF inválido -> 400
    @ExceptionHandler(ArquivoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> arquivoInvalido(ArquivoInvalidoException e) {
        return resposta(HttpStatus.BAD_REQUEST, List.of(e.getMessage()));
    }

    // Arquivo maior que o limite do application.properties -> 400
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> arquivoGrande(MaxUploadSizeExceededException e) {
        return resposta(HttpStatus.BAD_REQUEST, List.of("O currículo deve ter no máximo 5 MB."));
    }

    // Candidatura inexistente -> 404
    @ExceptionHandler(CandidaturaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrada(CandidaturaNaoEncontradaException e) {
        return resposta(HttpStatus.NOT_FOUND, List.of(e.getMessage()));
    }

    // Id inválido na URL (ex.: /api/candidaturas/abc) -> 400
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> idInvalido(MethodArgumentTypeMismatchException e) {
        return resposta(HttpStatus.BAD_REQUEST, List.of("O identificador informado é inválido."));
    }

    // Falha ao gravar o arquivo -> 500
    @ExceptionHandler(ArmazenamentoException.class)
    public ResponseEntity<Map<String, Object>> falhaArmazenamento(ArmazenamentoException e) {
        log.error("Falha de armazenamento", e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Não foi possível processar sua candidatura. Tente novamente."));
    }

    // Qualquer outro erro inesperado -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> erroGenerico(Exception e) throws Exception {
        // Erros padrão do Spring (404, 405, 415...) seguem o tratamento normal dele
        if (e instanceof org.springframework.web.ErrorResponse) {
            throw e;
        }
        log.error("Erro inesperado", e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Erro interno. Tente novamente mais tarde."));
    }

    private ResponseEntity<Map<String, Object>> resposta(HttpStatus status, List<String> erros) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", status.value());
        corpo.put("erros", erros);
        return ResponseEntity.status(status).body(corpo);
    }
}