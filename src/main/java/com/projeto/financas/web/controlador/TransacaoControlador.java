package com.projeto.financas.web.controlador;

import com.projeto.financas.aplicacao.dto.ResumoFinanceiroDTO;
import com.projeto.financas.aplicacao.dto.TransacaoRequisicaoDTO;
import com.projeto.financas.aplicacao.dto.TransacaoRespostaDTO;
import com.projeto.financas.aplicacao.servico.TransacaoServico;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoControlador {

    private final TransacaoServico transacaoServico;

    public TransacaoControlador(TransacaoServico transacaoServico) {
        this.transacaoServico = transacaoServico;
    }

    @PostMapping
    public ResponseEntity<TransacaoRespostaDTO> registrar(@RequestBody @Valid TransacaoRequisicaoDTO requisicao) {
        var transacao = transacaoServico.registrar(
                requisicao.descricao(),
                requisicao.valor(),
                requisicao.tipo(),
                requisicao.categoria(),
                requisicao.data()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TransacaoRespostaDTO.apartirDe(transacao));
    }

    @GetMapping
    public List<TransacaoRespostaDTO> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return transacaoServico.listarPorPeriodo(inicio, fim)
                .stream()
                .map(TransacaoRespostaDTO::apartirDe)
                .toList();
    }

    @GetMapping("/categoria/{categoria}/total")
    public BigDecimal totalPorCategoria(
            @PathVariable String categoria,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return transacaoServico.consultarTotalPorCategoria(categoria, inicio, fim);
    }

    @GetMapping("/resumo")
    public ResumoFinanceiroDTO resumo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim
    ) {
        return transacaoServico.gerarResumo(inicio, fim);
    }
}
