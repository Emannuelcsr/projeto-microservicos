package br.com.projetosecsr.pedidos.notificacao.config;

import java.util.HashMap;
import java.util.Map;

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
 * Classe de configuração do RabbitMQ usada pelo microsserviço de notificação.
 *
 * <p>Ela cria as estruturas necessárias para o serviço receber mensagens
 * vindas do microsserviço de pedidos.</p>
 *
 * <p>Essa configuração também cria uma DLQ, ou seja, uma fila de erro.
 * Quando uma mensagem não conseguir ser processada corretamente na fila
 * principal, ela poderá ser enviada para essa fila separada.</p>
 */
@Configuration
public class RabbitMQConfig {
	
	/**
	 * Nome da exchange principal.
	 *
	 * <p>Esse valor vem do application.properties.</p>
	 */
	@Value("${rabbitmq.exchange.name}")
	private String exchangeName;
	
	/**
	 * Nome da exchange de erro, chamada DLX.
	 *
	 * <p>DLX significa Dead Letter Exchange.</p>
	 *
	 * <p>Ela recebe mensagens que deram problema na fila principal.</p>
	 */
	@Value("${rabbitmq.exchange.dlx.name}")
	private String exchangeDlxName;
	
	/**
	 * Nome da fila principal de notificação.
	 *
	 * <p>Essa fila recebe as mensagens normais de pedidos.</p>
	 */
	@Value("${rabbitmq.queue.name}")
	private String queueName;
	
	/**
	 * Nome da fila de erro, chamada DLQ.
	 *
	 * <p>DLQ significa Dead Letter Queue.</p>
	 *
	 * <p>Ela guarda mensagens que não foram processadas corretamente.</p>
	 */
	@Value("${rabbitmq.queue.dlq.name}")
	private String queueDlqName;

	/**
	 * Cria a exchange principal do tipo Fanout.
	 *
	 * <p>A FanoutExchange envia a mesma mensagem para todas as filas
	 * conectadas a ela.</p>
	 *
	 * @return exchange principal usada para pedidos
	 */
	@Bean
	public FanoutExchange pedidosExchanges() {
		
		/*
		 * Cria a exchange principal usando o nome configurado
		 * no application.properties.
		 */
		return new FanoutExchange(exchangeName);
	}
	
	/**
	 * Cria a exchange de erro do tipo Fanout.
	 *
	 * <p>Essa exchange será usada quando uma mensagem precisar sair
	 * da fila principal e ir para o fluxo de erro.</p>
	 *
	 * @return exchange DLX
	 */
	@Bean
	public FanoutExchange pedidosDlxExchanges() {
		
		/*
		 * Cria a exchange de dead letter usando o nome configurado
		 * no application.properties.
		 */
		return new FanoutExchange(exchangeDlxName);
	}
	
	/**
	 * Cria a fila principal de notificação.
	 *
	 * <p>Essa fila recebe as mensagens de pedido criado.</p>
	 *
	 * <p>Ela também recebe uma configuração especial:
	 * caso uma mensagem seja rejeitada ou não consiga ser processada,
	 * essa mensagem será enviada para a exchange DLX.</p>
	 *
	 * @return fila principal de notificação
	 */
	@Bean
	public Queue notificacaoQueue() {

		/*
		 * Cria um mapa de argumentos extras para configurar a fila.
		 *
		 * O RabbitMQ usa esses argumentos para adicionar comportamentos
		 * especiais à fila.
		 */
		Map<String, Object> argumentos = new HashMap<>();

		/*
		 * Define para qual exchange a mensagem deve ir
		 * quando virar dead letter.
		 *
		 * Em linguagem simples:
		 * se der erro no processamento, manda para a exchange de erro.
		 */
		argumentos.put("x-dead-letter-exchange", exchangeDlxName);
		
		/*
		 * Cria a fila principal.
		 *
		 * Parâmetros:
		 * queueName  -> nome da fila
		 * true       -> fila durável, continua existindo após reiniciar o RabbitMQ
		 * false      -> não é exclusiva, outros consumidores podem usar
		 * false      -> não será apagada automaticamente
		 * argumentos -> configura a DLX da fila
		 */
	    return new Queue(queueName, true, false, false, argumentos);
	}
	
	/**
	 * Cria a fila de erro, chamada DLQ.
	 *
	 * <p>Essa fila recebe mensagens que não foram processadas
	 * corretamente na fila principal.</p>
	 *
	 * @return fila de dead letter
	 */
	@Bean
	public Queue notificacaoDlqQueue() {

		/*
		 * Cria a fila de erro usando o nome configurado
		 * no application.properties.
		 */
	    return new Queue(queueDlqName);
	}
	
	/**
	 * Liga a fila principal à exchange principal.
	 *
	 * <p>Sem esse binding, a exchange principal receberia a mensagem,
	 * mas a fila de notificação não receberia nada.</p>
	 *
	 * @return binding entre exchange principal e fila principal
	 */
	@Bean
	public Binding binding (){
		
		/*
		 * Conecta a fila principal notificacaoQueue()
		 * com a exchange principal pedidosExchanges().
		 */
		return BindingBuilder.bind(notificacaoQueue()).to(pedidosExchanges());
	}
	
	/**
	 * Liga a fila de erro à exchange de erro.
	 *
	 * <p>Quando uma mensagem for enviada para a DLX, esse binding garante
	 * que ela chegue na DLQ.</p>
	 *
	 * @return binding entre exchange DLX e fila DLQ
	 */
	@Bean
	public Binding bindingDlq (){
		
		/*
		 * Conecta a fila de erro notificacaoDlqQueue()
		 * com a exchange de erro pedidosDlxExchanges().
		 */
		return BindingBuilder.bind(notificacaoDlqQueue()).to(pedidosDlxExchanges());
	}
	
	/**
	 * Cria o RabbitAdmin.
	 *
	 * <p>O RabbitAdmin é responsável por declarar automaticamente
	 * exchanges, filas e bindings no RabbitMQ.</p>
	 *
	 * @param connectionFactory fábrica de conexão com o RabbitMQ
	 * @return administrador do RabbitMQ
	 */
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		
		/*
		 * Cria o administrador usando a conexão configurada pelo Spring.
		 */
		return new RabbitAdmin(connectionFactory);
	}
	
	/**
	 * Cria o conversor de mensagens para JSON.
	 *
	 * <p>Esse conversor permite que o Spring transforme objetos Java
	 * em JSON e JSON em objetos Java.</p>
	 *
	 * @return conversor JSON usado pelo RabbitMQ
	 */
	@Bean
	public MessageConverter messageConverter () {
		
		/*
		 * Usa o Jackson como conversor padrão de JSON.
		 */
		return new Jackson2JsonMessageConverter();
	}
	
	/**
	 * Cria e configura o RabbitTemplate.
	 *
	 * <p>O RabbitTemplate é usado para enviar mensagens para o RabbitMQ.</p>
	 *
	 * @param connectionFactory fábrica de conexão com o RabbitMQ
	 * @param converter conversor usado para transformar mensagens em JSON
	 * @return RabbitTemplate configurado
	 */
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
		
		/*
		 * Cria o RabbitTemplate usando a conexão do RabbitMQ.
		 */
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		
		/*
		 * Define o conversor JSON para as mensagens enviadas.
		 */
		rabbitTemplate.setMessageConverter(converter);
		
		/*
		 * Retorna o objeto pronto para enviar mensagens.
		 */
		return rabbitTemplate;
		
	}
	
	/**
	 * Inicializa as estruturas do RabbitMQ quando a aplicação termina de subir.
	 *
	 * <p>Esse listener roda quando o Spring Boot dispara o evento
	 * ApplicationReadyEvent.</p>
	 *
	 * @param admin administrador responsável por declarar as estruturas
	 * @return listener executado quando a aplicação estiver pronta
	 */
	@Bean
	public ApplicationListener<ApplicationReadyEvent>  applicationListener (RabbitAdmin admin){
		
		/*
		 * Quando a aplicação estiver pronta, força o RabbitAdmin
		 * a criar exchanges, filas e bindings configurados nesta classe.
		 */
		return event -> admin.initialize();
	}
}