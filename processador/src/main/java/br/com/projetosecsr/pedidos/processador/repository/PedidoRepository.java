package br.com.projetosecsr.pedidos.processador.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projetosecsr.pedidos.processador.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, UUID	> {

}
