package br.com.projetosecsr.pedidos.processador.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.projetosecsr.pedidos.processador.entity.ItemPedido;
import br.com.projetosecsr.pedidos.processador.entity.Pedido;
import br.com.projetosecsr.pedidos.processador.repository.ItemPedidoRepository;

@Service
public class ItemPedidoService {
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	public List<ItemPedido> save(List<ItemPedido> itens) {

		
		
		return  itemPedidoRepository.saveAll(itens);
	}

	
	public void save(ItemPedido itemPedido) {
		
		itemPedidoRepository.save(itemPedido);
	}
	
	
	public void updatedItemPedido(List<ItemPedido> itemPedidos, Pedido pedido) {

		itemPedidos.forEach(item -> {
			item.setPedido(pedido);//informando ao item o seu pedido
			this.save(item);
			
		});
		
	}
	
	
	
}
