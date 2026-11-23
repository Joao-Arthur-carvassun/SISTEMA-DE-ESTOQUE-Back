package com.empresa.estoque.exception;

public class ItemDuplicadoException extends RuntimeException {

    public ItemDuplicadoException(String nome) {
        super("Já existe um produto com esse nome: \"" + nome + "\". Use a entrada de estoque ou edite o existente.");
    }
}
