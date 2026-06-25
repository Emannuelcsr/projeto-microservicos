package br.com.projetosecsr.pedidos.processador.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.projetosecsr.pedidos.processador.entity.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Table(name = "pedido")
@Entity
@NoArgsConstructor // Gera construtor sem argumentos
@Data // Gera getters, setters, equals, hashCode e toString
public class Pedido {

	@Id
	private UUID id = UUID.randomUUID(); // Identificador único gerado na criação do objeto
	
	private String cliente;
	@OneToMany(mappedBy = "pedido")
	private List<ItemPedido> itens = new ArrayList<ItemPedido>();
	
	@Column(name = "valor_total")
	private Double valorTotal;
	
	@Column(name = "email_notificacao")
	private String emailNotificacao;
	
	@Enumerated(EnumType.STRING)
	private Status status;

	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dataHora = LocalDateTime.now();

}
