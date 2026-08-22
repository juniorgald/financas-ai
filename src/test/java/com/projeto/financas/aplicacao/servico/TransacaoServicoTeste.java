package com.projeto.financas.aplicacao.servico;

import com.projeto.financas.dominio.modelo.Transacao;
import com.projeto.financas.dominio.modelo.TipoTransacao;
import com.projeto.financas.dominio.repositorio.TransacaoRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransacaoServicoTeste {

    @Mock
    private TransacaoRepositorio repositorio;

    @Test
    void deveRegistrarUmaTransacaoComSucesso() {
        var servico = new TransacaoServico(repositorio);
        var transacaoSalva = new Transacao("Salario", new BigDecimal("3000"), TipoTransacao.RECEITA, "salario", LocalDate.now());
        when(repositorio.save(any(Transacao.class))).thenReturn(transacaoSalva);

        var resultado = servico.registrar("Salario", new BigDecimal("3000"), TipoTransacao.RECEITA, "salario", LocalDate.now());

        assertThat(resultado.getDescricao()).isEqualTo("Salario");
        assertThat(resultado.getTipo()).isEqualTo(TipoTransacao.RECEITA);
        verify(repositorio).save(any(Transacao.class));
    }

    @Test
    void deveCalcularTotalPorCategoriaSomandoOsValores() {
        var servico = new TransacaoServico(repositorio);
        var inicio = LocalDate.now().withDayOfMonth(1);
        var fim = LocalDate.now();
        var transacoes = List.of(
                new Transacao("Mercado", new BigDecimal("150.00"), TipoTransacao.DESPESA, "alimentacao", LocalDate.now()),
                new Transacao("Restaurante", new BigDecimal("80.00"), TipoTransacao.DESPESA, "alimentacao", LocalDate.now())
        );
        when(repositorio.findByCategoriaIgnoreCaseAndDataBetween("alimentacao", inicio, fim)).thenReturn(transacoes);

        var total = servico.consultarTotalPorCategoria("alimentacao", inicio, fim);

        assertThat(total).isEqualByComparingTo("230.00");
    }
}
