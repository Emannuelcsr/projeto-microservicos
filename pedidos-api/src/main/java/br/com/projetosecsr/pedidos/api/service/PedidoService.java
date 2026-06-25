package br.com.projetosecsr.pedidos.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.projetosecsr.pedidos.api.entity.Pedido;

@Service
public class PedidoService {
	
	private final Logger logger = LoggerFactory.getLogger(PedidoService.class);
	
	@Value("${rabbitmq.exchange.name}")
	private String exchangeName; 
	
	private final RabbitTemplate rabbitTemplate;
	
	public PedidoService(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}


	public Pedido enfileirarPedido(Pedido pedido) {
		
		rabbitTemplate.convertAndSend(exchangeName,"",pedido);// envia para o RabbitMQ
		logger.info("Pedido enfileirado: {}",pedido.toString());
		
		return pedido;
		
	}
	
}
