package br.com.projetosecsr.pedidos.notificacao.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import br.com.projetosecsr.pedidos.notificacao.entity.Pedido;
import br.com.projetosecsr.pedidos.notificacao.service.EmailService;

@Component
public class PedidoListener {
	
	private final Logger logger = LoggerFactory.getLogger(PedidoListener.class);
	private final EmailService emailService;
	
	public PedidoListener(EmailService emailService) {
		this.emailService = emailService;
	}
	
	
	
	@RabbitListener(queues = "pedidos.v1.pedido-criado-gerar-notificacao")
	public void enviarNotificacao (Pedido pedido) {
		
		logger.info("Tentando consumir a msg");
		if(pedido.getValorTotal()>200) {
			
			throw new RuntimeException("Valor muito atlo");
		}
		logger.info("Notificacao gerada: {} ",pedido.toString());
		emailService.enviarEmail(pedido);
		
	}

}
