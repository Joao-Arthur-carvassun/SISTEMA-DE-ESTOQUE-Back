package com.empresa.estoque.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome pode ter no máximo 150 caracteres")
        String nome,

        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 0, message = "A quantidade não pode ser negativa")
        Integer quantidade,

        @NotNull(message = "A quantidade mínima é obrigatória")
        @Min(value = 0, message = "A quantidade mínima não pode ser negativa")
        Integer quantidadeMinima
) {
}
