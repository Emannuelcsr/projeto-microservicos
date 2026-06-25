package br.com.projetosecsr.pedidos.processador.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.projetosecsr.pedidos.processador.entity.Pedido;
import br.com.projetosecsr.pedidos.processador.entity.enums.Status;
import br.com.projetosecsr.pedidos.processador.repository.PedidoRepository;
import br.com.projetosecsr.pedidos.processador.service.PedidoService;

@Component
public class PedidoListener {

    private final PedidoRepository pedidoRepository;

	private final Logger logger = LoggerFactory.getLogger(PedidoListener.class);
	
	@Autowired
	private PedidoService pedidoService;


    PedidoListener(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }
	
	
	
	@RabbitListener(queues = "pedidos.v1.pedido-criado-gerar-processamento")
	public void salvarPedido(Pedido pedido) {
		logger.info("Mensagem recebida do RabbitMQ");
		pedido.setStatus(Status.PROCESSADO);
		logger.info("Pedido processado : {}",pedido.toString());
		
		pedidoService.salvar(pedido);
		
	}
	
}
