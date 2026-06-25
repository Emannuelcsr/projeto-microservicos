package br.com.projetosecsr.pedidos.processador.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "produto")
@Entity
@NoArgsConstructor  // Gera construtor sem argumentos
@Data // Gera getters, setters, equals, hashCode e toString
public class Produto {
	
	@Id
    private UUID id = UUID.randomUUID(); // Identificador único gerado na criação do objeto
    private Double valor; // Valor do produto
    private String nome; // Nome do produto

}
