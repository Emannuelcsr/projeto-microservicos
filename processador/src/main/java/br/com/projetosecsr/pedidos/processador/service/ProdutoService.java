package br.com.projetosecsr.pedidos.processador.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.projetosecsr.pedidos.processador.entity.ItemPedido;
import br.com.projetosecsr.pedidos.processador.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	public void save(List<ItemPedido> itens) {

		
		   for (ItemPedido item : itens) {

		        produtoRepository.save(item.getProduto());
		    }
	}
	
}
