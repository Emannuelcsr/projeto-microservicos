package br.com.projetosecsr.pedidos.processador.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.projetosecsr.pedidos.processador.entity.ItemPedido;
import br.com.projetosecsr.pedidos.processador.entity.Pedido;
import br.com.projetosecsr.pedidos.processador.repository.PedidoRepository;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoService itemPedidoService;
	
	private Logger logger = LoggerFactory.getLogger(PedidoService.class);
	
	
	
	public void salvar(Pedido pedido) {
		
		//salvamos os produtos
		produtoService.save(pedido.getItens());
		
		//salvamos os itens do pedido		
		List<ItemPedido> itemPedidos = itemPedidoService.save(pedido.getItens());
		
		//salvamos o pedido
		pedidoRepository.save(pedido);
		
		//atualiza o item pedido definindo o pedido ao qual ele pertence
		itemPedidoService.updatedItemPedido(itemPedidos,pedido);
		
		logger.info("Pedido salvo: {}",pedido.toString());
		
	}
	
	
	
}
