package com.empresa.estoque.controller;

import com.empresa.estoque.dto.ItemRequest;
import com.empresa.estoque.dto.ItemResponse;
import com.empresa.estoque.dto.MovimentacaoRequest;
import com.empresa.estoque.dto.MovimentacaoResponse;
import com.empresa.estoque.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemResponse> criar(@Valid @RequestBody ItemRequest request) {
        ItemResponse criado = service.criar(request);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @GetMapping
    public List<ItemResponse> listar(@RequestParam(required = false)    String nome) {
        return service.listar(nome);
    }

    @GetMapping("/amount")
    public ResponseEntity<Long> getAmountItens() {
        long total = service.amountItens();
        return ResponseEntity.ok(total);

    }

    @GetMapping("/movimentacoes")
    public List<MovimentacaoResponse> movimentacoes() {
        return service.listarMovimentacoes();
    }

    @GetMapping("/alertas")
    public List<ItemResponse> alertas() {
        return service.listarAlertas();
    }

    @GetMapping("/{id}")
    public ItemResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public ItemResponse atualizar(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PostMapping("/{id}/entrada")
    public ItemResponse entrada(@PathVariable Long id, @Valid @RequestBody MovimentacaoRequest request) {
        return service.registrarEntrada(id, request.quantidade());
    }

    @PostMapping("/{id}/saida")
    public ItemResponse saida(@PathVariable Long id, @Valid @RequestBody MovimentacaoRequest request) {
        return service.registrarSaida(id, request.quantidade());
    }
}
