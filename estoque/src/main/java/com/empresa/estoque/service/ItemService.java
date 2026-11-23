package com.empresa.estoque.service;

import com.empresa.estoque.dto.ItemRequest;
import com.empresa.estoque.dto.ItemResponse;
import com.empresa.estoque.dto.MovimentacaoResponse;
import com.empresa.estoque.exception.EstoqueInsuficienteException;
import com.empresa.estoque.exception.ItemDuplicadoException;
import com.empresa.estoque.exception.ItemNaoEncontradoException;
import com.empresa.estoque.model.Item;
import com.empresa.estoque.model.Movimentacao;
import com.empresa.estoque.repository.ItemRepository;
import com.empresa.estoque.repository.MovimentacaoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class ItemService {

    private final ItemRepository repository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ItemService(ItemRepository repository, MovimentacaoRepository movimentacaoRepository) {
        this.repository = repository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public ItemResponse criar(ItemRequest request) {
        validarNomeUnico(request.nome(), null);
        Item item = new Item();
        preencher(item, request);
        Item salvo = repository.save(item);
        if (salvo.getQuantidade() > 0) {
            registrarMovimentacao(salvo, "CADASTRO", salvo.getQuantidade());
        }
        return paraResposta(salvo);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listar(String nome) {
        List<Item> itens = (nome == null || nome.isBlank())
                ? repository.findAll(Sort.by("nome"))
                : repository.findByNomeContainingIgnoreCaseOrderByNome(nome.trim());
        return itens.stream().map(this::paraResposta).toList();
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> listarAlertas() {
        return repository.buscarApenasItensEmAlerta()
                .stream()
                .map(this::paraResposta)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponse buscar(Long id) {
        return paraResposta(buscarEntidade(id));
    }

    @Transactional
    public ItemResponse atualizar(Long id, ItemRequest request) {
        Item item = buscarEntidade(id);
        validarNomeUnico(request.nome(), id);
        preencher(item, request);
        return paraResposta(repository.save(item));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    @Transactional
    public ItemResponse registrarEntrada(Long id, int quantidade) {
        Item item = buscarEntidade(id);
        item.setQuantidade(item.getQuantidade() + quantidade);
        Item salvo = repository.save(item);
        registrarMovimentacao(salvo, "ENTRADA", quantidade);
        return paraResposta(salvo);
    }

    @Transactional
    public ItemResponse registrarSaida(Long id, int quantidade) {
        Item item = buscarEntidade(id);
        if (quantidade > item.getQuantidade()) {
            throw new EstoqueInsuficienteException(item.getNome(), item.getQuantidade(), quantidade);
        }
        item.setQuantidade(item.getQuantidade() - quantidade);
        Item salvo = repository.save(item);
        registrarMovimentacao(salvo, "SAIDA", quantidade);
        return paraResposta(salvo);
    }

    @Transactional(readOnly = true)
    public long amountItens(){
        return repository.count();
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listarMovimentacoes() {
        return movimentacaoRepository.findTop10ByOrderByDataHoraDescIdDesc()
                .stream()
                .map(MovimentacaoResponse::de)
                .toList();
    }

    private void registrarMovimentacao(Item item, String tipo, int quantidade) {
        movimentacaoRepository.save(new Movimentacao(item.getId(), item.getNome(), tipo, quantidade));
    }

    private Item buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new ItemNaoEncontradoException(id));
    }

    /** Recusa nomes que, normalizados, já existem (ignora maiúsculas, acentos e espaços extras). */
    private void validarNomeUnico(String nome, Long idIgnorado) {
        String novo = normalizar(nome);
        boolean existe = repository.findAll().stream()
                .filter(i -> idIgnorado == null || !idIgnorado.equals(i.getId()))
                .anyMatch(i -> normalizar(i.getNome()).equals(novo));
        if (existe) {
            throw new ItemDuplicadoException(nome.trim());
        }
    }

    static String normalizar(String nome) {
        String semAcento = Normalizer.normalize(nome, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private void preencher(Item item, ItemRequest request) {
        item.setNome(request.nome().trim());
        item.setQuantidade(request.quantidade());
        item.setQuantidadeMinima(request.quantidadeMinima());
    }

    private ItemResponse paraResposta(Item item) {
        return ItemResponse.de(item);
    }


}
