package com.empresa.estoque.repository;

import com.empresa.estoque.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByNomeContainingIgnoreCaseOrderByNome(String nome);

    @Query("select i from Item i where i.quantidade < i.quantidadeMinima order by i.nome")
    List<Item> buscarApenasItensEmAlerta();
}
