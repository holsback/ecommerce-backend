package com.meuecommerce.backend.controller;

import com.meuecommerce.backend.entities.Carrinho;
import com.meuecommerce.backend.service.CarrinhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/carrinhos")
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @PostMapping
    public ResponseEntity<Carrinho> criarCarrinho() {
        Carrinho carrinho = carrinhoService.criarCarrinho();
        return new ResponseEntity<>(carrinho, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarCarrinhoPorId(@PathVariable Long id) {
        Optional<Carrinho> carrinho = carrinhoService.buscarCarrinhoPorId(id);
        return carrinho.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/{carrinhoId}/itens/{produtoId}")
    public ResponseEntity<Void> adicionarItemAoCarrinho(@PathVariable Long carrinhoId,
                                                        @PathVariable Long produtoId,
                                                        @RequestParam int quantidade) {
        try {
            carrinhoService.adicionarItemAoCarrinho(carrinhoId, produtoId, quantidade);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{carrinhoId}/itens/{itemId}")
    public ResponseEntity<Void> removerItemDoCarrinho(@PathVariable Long carrinhoId,
                                                      @PathVariable Long itemId) {
        try {
            carrinhoService.removerItemDoCarrinho(carrinhoId, itemId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{carrinhoId}/itens/{itemId}")
    public ResponseEntity<Void> atualizarQuantidadeItemCarrinho(@PathVariable Long carrinhoId,
                                                                @PathVariable Long itemId,
                                                                @RequestParam int quantidade) {
        try {
            carrinhoService.atualizarQuantidadeItemCarrinho(carrinhoId, itemId, quantidade);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{carrinhoId}/itens")
    public ResponseEntity<Void> limparCarrinho(@PathVariable Long carrinhoId) {
        try {
            carrinhoService.limparCarrinho(carrinhoId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Ou outro status apropriado
        }
    }

    @PostMapping("/{carrinhoId}/finalizar")
    public ResponseEntity<String> finalizarCompra(@PathVariable Long carrinhoId) {
        try {
            carrinhoService.finalizarCompra(carrinhoId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}