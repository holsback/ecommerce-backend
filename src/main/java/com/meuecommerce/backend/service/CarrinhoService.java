package com.meuecommerce.backend.service;

import com.meuecommerce.backend.entities.Carrinho;
import com.meuecommerce.backend.entities.ItemCarrinho;
import com.meuecommerce.backend.entities.Produto;
import com.meuecommerce.backend.repository.CarrinhoRepository;
import com.meuecommerce.backend.repository.ItemCarrinhoRepository;
import com.meuecommerce.backend.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CarrinhoService {

    @Autowired
    private CarrinhoRepository carrinhoRepository;

    @Autowired
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    public Optional<Carrinho> buscarCarrinhoPorId(Long id) {
        return carrinhoRepository.findById(id);
    }

    public Carrinho criarCarrinho() {
        Carrinho carrinho = new Carrinho();
        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public void adicionarItemAoCarrinho(Long carrinhoId, Long produtoId, int quantidade) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com ID: " + carrinhoId));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        Optional<ItemCarrinho> itemExistente = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produtoId))
                .findFirst();

        if (itemExistente.isPresent()) {
            ItemCarrinho item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            itemCarrinhoRepository.save(item);
        } else {
            ItemCarrinho novoItem = new ItemCarrinho(carrinho, produto, quantidade);
            carrinho.getItens().add(novoItem);
            itemCarrinhoRepository.save(novoItem);
        }
        carrinhoRepository.save(carrinho); // Garante que a coleção de itens do carrinho seja persistida
    }

    @Transactional
    public void removerItemDoCarrinho(Long carrinhoId, Long itemId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com ID: " + carrinhoId));
        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item do carrinho não encontrado com ID: " + itemId));

        if (carrinho.getItens().contains(item)) {
            carrinho.getItens().remove(item);
            item.setCarrinho(null); // Desassocia o item do carrinho
            itemCarrinhoRepository.delete(item);
            carrinhoRepository.save(carrinho); // Atualiza o carrinho após remover o item
        } else {
            throw new RuntimeException("Item não encontrado no carrinho especificado.");
        }
    }

    @Transactional
    public void atualizarQuantidadeItemCarrinho(Long carrinhoId, Long itemId, int novaQuantidade) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com ID: " + carrinhoId));
        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item do carrinho não encontrado com ID: " + itemId));

        if (carrinho.getItens().contains(item)) {
            if (novaQuantidade > 0) {
                item.setQuantidade(novaQuantidade);
                itemCarrinhoRepository.save(item);
            } else {
                // Se a nova quantidade for zero ou negativa, podemos remover o item
                removerItemDoCarrinho(carrinhoId, itemId);
            }
        } else {
            throw new RuntimeException("Item não encontrado no carrinho especificado.");
        }
    }

    @Transactional
    public void limparCarrinho(Long carrinhoId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com ID: " + carrinhoId));
        carrinho.getItens().forEach(item -> item.setCarrinho(null)); // Desassocia os itens
        carrinho.getItens().clear();
        itemCarrinhoRepository.deleteAll(carrinho.getItens()); // Remove os itens do banco
        carrinhoRepository.save(carrinho); // Atualiza o carrinho
    }

    @Transactional
    public void finalizarCompra(Long carrinhoId) {
        Carrinho carrinho = carrinhoRepository.findById(carrinhoId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com ID: " + carrinhoId));
        if (carrinho.getItens().isEmpty()) {
            throw new RuntimeException("O carrinho está vazio. Adicione itens antes de finalizar a compra.");
        }
        for (ItemCarrinho item : carrinho.getItens()) {
            if (!produtoService.verificarEstoque(item.getProduto().getId(), item.getQuantidade())) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + item.getProduto().getNome());
            }
        }
        for (ItemCarrinho item : carrinho.getItens()) {
            produtoService.reduzirEstoque(item.getProduto().getId(), item.getQuantidade());
        }
        limparCarrinho(carrinhoId);
    }
}
