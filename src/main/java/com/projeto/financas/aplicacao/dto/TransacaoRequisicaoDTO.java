package com.projeto.financas.aplicacao.dto;

import com.projeto.financas.dominio.modelo.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransacaoRequisicaoDTO(
        @NotBlank String descricao,
        @NotNull @Positive BigDecimal valor,
        @NotNull TipoTransacao tipo,
        @NotBlank String categoria,
        @NotNull LocalDate data
) {
}
