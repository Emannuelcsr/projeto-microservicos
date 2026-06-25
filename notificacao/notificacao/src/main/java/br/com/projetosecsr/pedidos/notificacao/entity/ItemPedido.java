package br.com.projetosecsr.pedidos.notificacao.entity;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor  // Gera construtor sem argumentos
@Data // Gera getters, setters, equals, hashCode e toString
public class ItemPedido {
	
	private UUID id = UUID.randomUUID();  // Identificador único gerado na criação do objeto
	private Produto produto;
	private Integer quantidade;

}
