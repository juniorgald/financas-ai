package com.projeto.financas.aplicacao.dto;

import java.math.BigDecimal;

public record ResumoFinanceiroDTO(BigDecimal totalReceitas, BigDecimal totalDespesas, BigDecimal saldo) {
}