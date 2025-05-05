package com.meuecommerce.backend.repository;

import com.meuecommerce.backend.entities.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {
    //Buscar carrinho pelo id
    Optional<Carrinho> findById(Long id); // Alterado para Integer para corresponder ao tipo do ID na entidade

    //Buscar carrinho pelo id do usuario (CORREÇÃO)
    List<Carrinho> findByUsuario_Id(Long usuarioId);

    //Buscar por periodo
    List<Carrinho> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);
    //Buscar a partir de data
    List<Carrinho> findByDataCriacaoAfter(LocalDateTime data);
    //Buscar antes de data
    List<Carrinho> findByDataCriacaoBefore(LocalDateTime data);
}