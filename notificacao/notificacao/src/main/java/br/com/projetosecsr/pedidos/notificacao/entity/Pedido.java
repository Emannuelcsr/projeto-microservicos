package br.com.projetosecsr.pedidos.notificacao.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.projetosecsr.pedidos.notificacao.entity.enums.Status;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // Gera construtor sem argumentos
@Data // Gera getters, setters, equals, hashCode e toString
public class Pedido {

	private UUID id = UUID.randomUUID(); // Identificador único gerado na criação do objeto
	private String cliente;
	private List<ItemPedido> itens = new ArrayList<ItemPedido>();
	private Double valorTotal;
	private String emailNotificacao;
	private Status status = Status.EM_PROCESSAMENTO;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dataHora = LocalDateTime.now();

}
