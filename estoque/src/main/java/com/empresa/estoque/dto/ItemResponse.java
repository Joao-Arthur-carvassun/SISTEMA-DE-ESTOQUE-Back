package com.empresa.estoque.dto;

import com.empresa.estoque.model.Item;

import java.util.ArrayList;
import java.util.List;

public record ItemResponse(
        Long id,
        String nome,
        int quantidade,
        int quantidadeMinima,
        boolean estoqueBaixo,
        List<String> avisos
) {

    public static ItemResponse de(Item item) {
        boolean estoqueBaixo = item.getQuantidade() < item.getQuantidadeMinima();

        List<String> avisos = new ArrayList<>();
        if (estoqueBaixo) {
            avisos.add("Estoque baixo: restam " + item.getQuantidade()
                    + " (mínimo " + item.getQuantidadeMinima() + "). É preciso reabastecer.");
        }

        return new ItemResponse(
                item.getId(),
                item.getNome(),
                item.getQuantidade(),
                item.getQuantidadeMinima(),
                estoqueBaixo,
                avisos
        );
    }
}
