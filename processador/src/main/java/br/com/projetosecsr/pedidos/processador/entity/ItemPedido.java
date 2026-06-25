package br.com.projetosecsr.pedidos.processador.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "item_pedido")
@Entity
@NoArgsConstructor  // Gera construtor sem argumentos
@Data // Gera getters, setters, equals, hashCode e toString
public class ItemPedido {
	
	@Id
	private UUID id = UUID.randomUUID();  // Identificador único gerado na criação do objeto
	
	@ManyToOne
	private Produto produto;
	
	
	private Integer quantidade;

	@ManyToOne
	private Pedido pedido;
	
}
 