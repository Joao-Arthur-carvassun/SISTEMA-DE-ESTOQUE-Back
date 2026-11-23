package com.empresa.estoque.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MovimentacaoRequest(
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 1, message = "A quantidade deve ser pelo menos 1")
        Integer quantidade
) {
}
