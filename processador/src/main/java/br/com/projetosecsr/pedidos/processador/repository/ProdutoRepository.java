package br.com.projetosecsr.pedidos.processador.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.projetosecsr.pedidos.processador.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

}
