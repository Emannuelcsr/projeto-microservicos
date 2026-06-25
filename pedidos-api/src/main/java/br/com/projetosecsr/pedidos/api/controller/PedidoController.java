package br.com.projetosecsr.pedidos.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projetosecsr.pedidos.api.entity.Pedido;
import br.com.projetosecsr.pedidos.api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "pedidos",description = "Recurso para criar um novo pedido")
@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
	
	private final Logger logger = LoggerFactory.getLogger(PedidoController.class);
	
	@Autowired
	private  PedidoService pedidoService;
	

	
	
	
	@Operation(summary = "Cria um novo pedido",description = "Contem as operações para criar um novo pedido",
			responses = @ApiResponse(responseCode = "201",description = "Recurso criado com sucesso", 
			content = @Content (mediaType = "application/json",schema = @Schema(implementation = Pedido.class))))
	@PostMapping
	public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido){
		
		logger.info("Pedido Recebido: {}",pedido.toString());
		
		Pedido teste = new Pedido();

		logger.info(
		    "NOVO PEDIDO: id={}, status={}, dataHora={}",
		    teste.getId(),
		    teste.getStatus(),
		    teste.getDataHora()
		);
		
		pedido = pedidoService.enfileirarPedido(pedido);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
	
	}
	
	
	

}
