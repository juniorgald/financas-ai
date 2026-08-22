package com.projeto.financas.aplicacao.servico;

import com.projeto.financas.aplicacao.dto.ResumoFinanceiroDTO;
import com.projeto.financas.dominio.modelo.Transacao;
import com.projeto.financas.dominio.modelo.TipoTransacao;
import com.projeto.financas.dominio.repositorio.TransacaoRepositorio;
import com.projeto.financas.infraestrutura.excecao.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoServico {

    private final TransacaoRepositorio repositorio;

    public TransacaoServico(TransacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Transacao registrar(String descricao, BigDecimal valor, TipoTransacao tipo, String categoria, LocalDate data) {
        var transacao = new Transacao(descricao, valor, tipo, categoria, data != null ? data : LocalDate.now());
        return repositorio.save(transacao);
    }

    public List<Transacao> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        var dataInicio = inicio != null ? inicio : LocalDate.now().withDayOfMonth(1);
        var dataFim = fim != null ? fim : LocalDate.now();
        return repositorio.findByDataBetweenOrderByDataDesc(dataInicio, dataFim);
    }

    public BigDecimal consultarTotalPorCategoria(String categoria, LocalDate inicio, LocalDate fim) {
        var dataInicio = inicio != null ? inicio : LocalDate.now().withDayOfMonth(1);
        var dataFim = fim != null ? fim : LocalDate.now();
        return repositorio.findByCategoriaIgnoreCaseAndDataBetween(categoria, dataInicio, dataFim)
                .stream()
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ResumoFinanceiroDTO gerarResumo(LocalDate inicio, LocalDate fim) {
        var transacoes = listarPorPeriodo(inicio, fim);
        var totalReceitas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalDespesas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoTransacao.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var saldo = totalReceitas.subtract(totalDespesas);
        return new ResumoFinanceiroDTO(totalReceitas, totalDespesas, saldo);
    }

    public Transacao buscarPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Transacao nao encontrada com o id " + id));
    }
}
