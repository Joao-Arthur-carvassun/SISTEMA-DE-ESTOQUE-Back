package com.empresa.estoque.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String nome, int disponivel, int solicitado) {
        super("Estoque insuficiente de \"" + nome + "\": disponível " + disponivel
                + ", solicitado " + solicitado);
    }
}
