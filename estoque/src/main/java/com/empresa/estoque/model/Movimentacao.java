package com.empresa.estoque.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_nome", nullable = false)
    private String itemNome;

    /** CADASTRO, ENTRADA ou SAIDA */
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public Movimentacao() {
    }

    public Movimentacao(Long itemId, String itemNome, String tipo, int quantidade) {
        this.itemId = itemId;
        this.itemNome = itemNome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getItemId() { return itemId; }
    public String getItemNome() { return itemNome; }
    public String getTipo() { return tipo; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
}
