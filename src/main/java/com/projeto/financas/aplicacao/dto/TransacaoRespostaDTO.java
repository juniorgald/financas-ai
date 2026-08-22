package com.projeto.financas.aplicacao.dto;

import com.projeto.financas.dominio.modelo.Transacao;
import com.projeto.financas.dominio.modelo.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoRespostaDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        TipoTransacao tipo,
        String categoria,
        LocalDate data
) {

    public static TransacaoRespostaDTO apartirDe(Transacao transacao) {
        return new TransacaoRespostaDTO(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getTipo(),
                transacao.getCategoria(),
                transacao.getData()
        );
    }
}
