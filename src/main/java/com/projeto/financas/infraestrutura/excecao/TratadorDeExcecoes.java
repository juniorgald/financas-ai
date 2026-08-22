package com.projeto.financas.infraestrutura.excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeExcecoes {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {
        var erro = new ErroRespostaDTO(HttpStatus.NOT_FOUND.value(), excecao.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarErrosDeValidacao(MethodArgumentNotValidException excecao) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError erro : excecao.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        var erroResposta = new ErroRespostaDTO(HttpStatus.BAD_REQUEST.value(), "Dados invalidos", Instant.now(), erros);
        return ResponseEntity.badRequest().body(erroResposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroGenerico(Exception excecao) {
        var erro = new ErroRespostaDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro inesperado: " + excecao.getMessage());
        return ResponseEntity.internalServerError().body(erro);
    }
}
