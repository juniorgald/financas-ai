package com.projeto.financas.infraestrutura.excecao;

import java.time.Instant;
import java.util.Map;

public record ErroRespostaDTO(int status, String mensagem, Instant momento, Map<String, String> erros) {

    public ErroRespostaDTO(int status, String mensagem) {
        this(status, mensagem, Instant.now(), null);
    }
}
