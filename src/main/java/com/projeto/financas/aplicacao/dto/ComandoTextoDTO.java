package com.projeto.financas.aplicacao.dto;

import jakarta.validation.constraints.NotBlank;

public record ComandoTextoDTO(@NotBlank String mensagem) {
}
