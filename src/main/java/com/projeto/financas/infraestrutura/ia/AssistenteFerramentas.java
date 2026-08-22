package com.projeto.financas.infraestrutura.ia;

import com.projeto.financas.aplicacao.servico.TransacaoServico;
import com.projeto.financas.dominio.modelo.Transacao;
import com.projeto.financas.dominio.modelo.TipoTransacao;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssistenteFerramentas {

    private final TransacaoServico transacaoServico;

    public AssistenteFerramentas(TransacaoServico transacaoServico) {
        this.transacaoServico = transacaoServico;
    }

    @Tool(description = "Registra uma nova transacao financeira, receita ou despesa, no orcamento do usuario")
    public String registrarTransacao(
            @ToolParam(description = "Descricao curta da transacao, ex: almoco no restaurante") String descricao,
            @ToolParam(description = "Valor da transacao em reais, sempre positivo") BigDecimal valor,
            @ToolParam(description = "Tipo: RECEITA ou DESPESA") TipoTransacao tipo,
            @ToolParam(description = "Categoria da transacao, ex: alimentacao, transporte, lazer, salario") String categoria,
            @ToolParam(description = "Data da transacao no formato AAAA-MM-DD, usar hoje se nao informada", required = false) LocalDate data
    ) {
        var transacao = transacaoServico.registrar(descricao, valor, tipo, categoria, data);
        return "Transacao registrada: " + transacao.getDescricao() + " no valor de R$ " + transacao.getValor();
    }

    @Tool(description = "Lista as transacoes financeiras registradas em um periodo, usa o mes atual se as datas nao forem informadas")
    public String listarTransacoes(
            @ToolParam(description = "Data inicial no formato AAAA-MM-DD, opcional", required = false) LocalDate inicio,
            @ToolParam(description = "Data final no formato AAAA-MM-DD, opcional", required = false) LocalDate fim
    ) {
        List<Transacao> transacoes = transacaoServico.listarPorPeriodo(inicio, fim);
        if (transacoes.isEmpty()) {
            return "Nenhuma transacao encontrada no periodo informado";
        }
        return transacoes.stream()
                .map(t -> t.getData() + " - " + t.getDescricao() + " - " + t.getTipo() + " - R$ " + t.getValor())
                .collect(Collectors.joining("; "));
    }

    @Tool(description = "Consulta o total gasto ou recebido em uma categoria especifica dentro de um periodo")
    public String consultarTotalPorCategoria(
            @ToolParam(description = "Categoria a consultar, ex: alimentacao, transporte, lazer") String categoria,
            @ToolParam(description = "Data inicial no formato AAAA-MM-DD, opcional", required = false) LocalDate inicio,
            @ToolParam(description = "Data final no formato AAAA-MM-DD, opcional", required = false) LocalDate fim
    ) {
        BigDecimal total = transacaoServico.consultarTotalPorCategoria(categoria, inicio, fim);
        return "Total na categoria " + categoria + ": R$ " + total;
    }

    @Tool(description = "Gera um resumo financeiro do periodo, mostrando total de receitas, total de despesas e o saldo")
    public String gerarResumoFinanceiro(
            @ToolParam(description = "Data inicial no formato AAAA-MM-DD, opcional", required = false) LocalDate inicio,
            @ToolParam(description = "Data final no formato AAAA-MM-DD, opcional", required = false) LocalDate fim
    ) {
        var resumo = transacaoServico.gerarResumo(inicio, fim);
        return "Receitas: R$ " + resumo.totalReceitas() + ", Despesas: R$ " + resumo.totalDespesas() + ", Saldo: R$ " + resumo.saldo();
    }
}
