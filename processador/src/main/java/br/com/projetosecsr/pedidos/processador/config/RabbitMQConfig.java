package br.com.projetosecsr.pedidos.processador.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do RabbitMQ no microsserviço de notificação.
 *
 * <p>Função dessa classe:</p>
 * <p>Ela cria e configura os componentes necessários para o Spring conversar
 * com o RabbitMQ: exchange, fila, ligação entre eles, conversor JSON
 * e RabbitTemplate.</p>
 *
 * <p>Em linguagem simples:</p>
 * <p>Essa classe prepara a “caixa de entrada” da notificação no RabbitMQ.</p>
 */
@Configuration
public class RabbitMQConfig {
	
	/**
	 * Nome da exchange configurado no application.properties.
	 *
	 * <p>Exchange é como uma central que recebe mensagens
	 * e distribui para as filas ligadas a ela.</p>
	 */
	@Value("${rabbitmq.exchange.name}")
	private String exchangeName;
	
	/**
	 * Nome da fila configurado no application.properties.
	 *
	 * <p>Essa fila vai receber as mensagens de pedidos
	 * para o serviço de notificação consumir.</p>
	 */
	@Value("${rabbitmq.queue.name}")
	private String queueName;
	
	/**
	 * Cria a exchange do tipo Fanout.
	 *
	 * <p>FanoutExchange envia a mesma mensagem para todas as filas
	 * ligadas a ela.</p>
	 *
	 * @return exchange de pedidos
	 */
	@Bean
	public FanoutExchange pedidosExchanges() {
		
		/*
		 * Cria uma exchange usando o nome vindo do application.properties.
		 */
		return new FanoutExchange(exchangeName);
	}
	
	/**
	 * Cria a fila de notificação.
	 *
	 * <p>É nessa fila que o serviço de notificação vai receber
	 * as mensagens publicadas no RabbitMQ.</p>
	 *
	 * @return fila de notificação
	 */
	@Bean
	public Queue processadorQueue() {

		/*
		 * Cria uma fila com o nome vindo do application.properties.
		 */
	    return new Queue(queueName);
	}
	
	/**
	 * Liga a fila de notificação à exchange de pedidos.
	 *
	 * <p>Sem esse binding, a exchange receberia mensagens,
	 * mas a fila não receberia nada.</p>
	 *
	 * @return ligação entre fila e exchange
	 */
	@Bean
	public Binding binding (){
		
		/*
		 * Conecta:
		 * fila notificacaoQueue -> exchange pedidosExchanges
		 */
		return BindingBuilder.bind(processadorQueue()).to(pedidosExchanges());
	}
	
	/**
	 * Cria o RabbitAdmin.
	 *
	 * <p>RabbitAdmin é usado para declarar automaticamente
	 * exchanges, filas e bindings no RabbitMQ.</p>
	 *
	 * @param connectionFactory fábrica de conexão com o RabbitMQ
	 * @return administrador do RabbitMQ
	 */
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		
		/*
		 * Usa a conexão configurada pelo Spring para administrar
		 * estruturas no RabbitMQ.
		 */
		return new RabbitAdmin(connectionFactory);
	}
	
	/**
	 * Cria o conversor de mensagens para JSON.
	 *
	 * <p>Esse conversor transforma objetos Java em JSON
	 * e JSON em objetos Java.</p>
	 *
	 * @return conversor de mensagem JSON
	 */
	@Bean
	public MessageConverter messageConverter () {
		
		/*
		 * Usa Jackson para converter mensagens automaticamente.
		 */
		return new Jackson2JsonMessageConverter();
	}
	
	/**
	 * Cria e configura o RabbitTemplate.
	 *
	 * <p>RabbitTemplate é o objeto usado pelo Spring para enviar
	 * mensagens para o RabbitMQ.</p>
	 *
	 * @param connectionFactory fábrica de conexão com RabbitMQ
	 * @param converter conversor que transforma objeto Java em JSON
	 * @return RabbitTemplate configurado
	 */
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
		
		/*
		 * Cria o RabbitTemplate usando a conexão com o RabbitMQ.
		 */
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		
		/*
		 * Define que as mensagens devem ser convertidas para JSON.
		 */
		rabbitTemplate.setMessageConverter(converter);
		
		/*
		 * Retorna o RabbitTemplate pronto para uso.
		 */
		return rabbitTemplate;
		
	}
	
	/**
	 * Executa a inicialização do RabbitAdmin quando a aplicação terminar de subir.
	 *
	 * <p>Esse método garante que fila, exchange e binding sejam criados
	 * no RabbitMQ quando o Spring Boot estiver pronto.</p>
	 *
	 * @param admin administrador do RabbitMQ
	 * @return listener executado quando a aplicação estiver pronta
	 */
	@Bean
	public ApplicationListener<ApplicationReadyEvent>  applicationListener (RabbitAdmin admin){
		
		/*
		 * Quando a aplicação estiver pronta,
		 * chama admin.initialize().
		 *
		 * Isso força a criação das estruturas configuradas:
		 * exchange, fila e binding.
		 */
		return event -> admin.initialize();
	}
}


/*Resumo didático

Essa classe prepara o RabbitMQ para o serviço de notificação.

Ela cria:

uma exchange
uma fila
uma ligação entre exchange e fila
um conversor JSON
um RabbitTemplate
um RabbitAdmin

Em linguagem simples:
ela monta o caminho por onde a mensagem do pedido vai chegar até a notificação.*/