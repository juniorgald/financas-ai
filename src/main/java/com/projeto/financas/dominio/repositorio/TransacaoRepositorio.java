package com.projeto.financas.dominio.repositorio;

import com.projeto.financas.dominio.modelo.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepositorio extends JpaRepository<Transacao, Long> {

    List<Transacao> findByDataBetweenOrderByDataDesc(LocalDate inicio, LocalDate fim);

    List<Transacao> findByCategoriaIgnoreCaseAndDataBetween(String categoria, LocalDate inicio, LocalDate fim);
}
