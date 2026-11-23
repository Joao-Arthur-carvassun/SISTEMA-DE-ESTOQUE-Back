package com.empresa.estoque.repository;

import com.empresa.estoque.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findTop10ByOrderByDataHoraDescIdDesc();
}
