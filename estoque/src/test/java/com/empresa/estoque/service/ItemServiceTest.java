package com.empresa.estoque.service;

import com.empresa.estoque.dto.ItemResponse;
import com.empresa.estoque.exception.EstoqueInsuficienteException;
import com.empresa.estoque.dto.ItemRequest;
import com.empresa.estoque.exception.ItemDuplicadoException;
import com.empresa.estoque.exception.ItemNaoEncontradoException;
import com.empresa.estoque.model.Item;
import com.empresa.estoque.repository.ItemRepository;
import com.empresa.estoque.repository.MovimentacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    private ItemRepository repository;
    private ItemService service;

    @BeforeEach
    void preparar() {
        repository = mock(ItemRepository.class);
        service = new ItemService(repository, mock(MovimentacaoRepository.class));
        when(repository.save(any(Item.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
    }

    private Item item(int quantidade, int minimo) {
        Item item = new Item();
        item.setId(1L);
        item.setNome("Papel A4");
        item.setQuantidade(quantidade);
        item.setQuantidadeMinima(minimo);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        return item;
    }

    @Test
    void saidaQueDeixaEstoqueAbaixoDoMinimoGeraAviso() {
        item(10, 5);

        ItemResponse resposta = service.registrarSaida(1L, 6);

        assertEquals(4, resposta.quantidade());
        assertTrue(resposta.estoqueBaixo());
        assertFalse(resposta.avisos().isEmpty());
    }

    @Test
    void saidaMaiorQueOEstoqueEhRecusada() {
        item(3, 1);

        assertThrows(EstoqueInsuficienteException.class, () -> service.registrarSaida(1L, 4));
    }

    @Test
    void entradaSomaNaQuantidadeEAcabaComOAviso() {
        item(2, 5);

        ItemResponse resposta = service.registrarEntrada(1L, 10);

        assertEquals(12, resposta.quantidade());
        assertFalse(resposta.estoqueBaixo());
    }

    @Test
    void buscarPorIdRetornaOItemCorreto() {
        item(8, 2);

        ItemResponse resposta = service.buscar(1L);

        assertEquals(1L, resposta.id());
        assertEquals("Papel A4", resposta.nome());
    }

    @Test
    void buscarPorIdInexistenteLancaErro() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ItemNaoEncontradoException.class, () -> service.buscar(99L));
    }

    @Test
    void alertasVemDaConsultaDeItensEmAlerta() {
        Item baixo = item(1, 5);
        when(repository.buscarApenasItensEmAlerta()).thenReturn(List.of(baixo));

        List<ItemResponse> alertas = service.listarAlertas();

        assertEquals(1, alertas.size());
        assertTrue(alertas.get(0).estoqueBaixo());
    }

    @Test
    void nomeIgualIgnorandoAcentoCaixaEEspacosEhRecusado() {
        Item existente = new Item();
        existente.setId(1L);
        existente.setNome("Café Torrado");
        when(repository.findAll()).thenReturn(List.of(existente));

        assertThrows(ItemDuplicadoException.class,
                () -> service.criar(new ItemRequest("  cafe   TORRADO ", 1, 1)));
    }

    @Test
    void editarOProprioItemSemMudarONomeEhPermitido() {
        Item existente = item(5, 1);
        when(repository.findAll()).thenReturn(List.of(existente));

        service.atualizar(1L, new ItemRequest("papel a4", 5, 2));
    }
}
