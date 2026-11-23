package com.empresa.estoque.dto;

import com.empresa.estoque.model.Movimentacao;

import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        String itemNome,
        String tipo,
        int quantidade,
        LocalDateTime dataHora
) {

    public static MovimentacaoResponse de(Movimentacao m) {
        return new MovimentacaoResponse(m.getId(), m.getItemNome(), m.getTipo(), m.getQuantidade(), m.getDataHora());
    }
}
