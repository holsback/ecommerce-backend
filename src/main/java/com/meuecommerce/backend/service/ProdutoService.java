package com.meuecommerce.backend.service;

import com.meuecommerce.backend.entities.Produto;
import com.meuecommerce.backend.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarProdutoPorId(Integer id) {
        return produtoRepository.findById(Long.valueOf(id));
    }

    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public void deletarProduto(Integer id) {
        produtoRepository.deleteById(Long.valueOf(id));
    }

    public boolean verificarEstoque(Integer produtoId, int quantidadeNecessaria) {
        Optional<Produto> produtoOptional = produtoRepository.findById(Long.valueOf(produtoId));
        return produtoOptional.map(produto -> produto.getEstoque() >= quantidadeNecessaria).orElse(false);
    }

    public void reduzirEstoque(Integer produtoId, int quantidade) {
        produtoRepository.findById(Long.valueOf(produtoId)).ifPresent(produto -> {
            if (produto.getEstoque() >= quantidade) {
                produto.setEstoque(produto.getEstoque() - quantidade);
                produtoRepository.save(produto);
            } else {
                throw new RuntimeException("Estoque insuficiente para o produto com ID: " + produtoId);
            }
        });
    }

}
