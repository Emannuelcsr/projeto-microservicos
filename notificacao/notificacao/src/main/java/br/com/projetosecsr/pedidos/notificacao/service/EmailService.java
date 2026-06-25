package br.com.projetosecsr.pedidos.notificacao.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.projetosecsr.pedidos.notificacao.entity.Pedido;

@Service
public class EmailService {
	
	private final JavaMailSender mailSender;
	
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	
	}
		
	
	
	public void enviarEmail(Pedido pedido) {
		
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		
		simpleMailMessage.setFrom("pedido-api@company.com");
		simpleMailMessage.setTo(pedido.getEmailNotificacao());
		simpleMailMessage.setSubject("pedido de compra");
		simpleMailMessage.setText(this.gerarMensagem(pedido));
		
		mailSender.send(simpleMailMessage);
		
	}



	private  String gerarMensagem(Pedido pedido) {

		String pedidoId = pedido.getId().toString();
		
		String cliente = pedido.getCliente();
		
		String valor = String.valueOf(pedido.getValorTotal());
		
		String status = pedido.getStatus().name();
		
		
		return String.format("Olá %s seu pedido de número %s no valor de R$ %s foi realizado com sucesso. \n Status: %s. ", cliente,pedidoId,valor,status);
	}

}
