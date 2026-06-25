
package br.com.projetosecsr.pedidos.api.config;

// Representa uma Exchange do RabbitMQ.
// A Exchange recebe a mensagem e decide para quais filas ela será enviada.
import org.springframework.amqp.core.Exchange;

// É um tipo de Exchange que envia a mensagem para todas as filas conectadas a ela.
import org.springframework.amqp.core.FanoutExchange;

// Representa a conexão entre a aplicação Spring e o servidor RabbitMQ.
// O Spring Boot cria esse objeto automaticamente usando o application.properties.
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

// Objeto usado para criar e administrar recursos dentro do RabbitMQ,
// como Exchanges, filas e ligações entre elas.
import org.springframework.amqp.rabbit.core.RabbitAdmin;

// Objeto que será usado pela aplicação para enviar mensagens ao RabbitMQ.
import org.springframework.amqp.rabbit.core.RabbitTemplate;

// Conversor que transforma objetos Java em JSON antes de enviá-los ao RabbitMQ.
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

// Interface que representa um conversor de mensagens.
// Nosso método devolverá um conversor que trabalha com JSON.
import org.springframework.amqp.support.converter.MessageConverter;

// Permite buscar um valor que está no application.properties.
import org.springframework.beans.factory.annotation.Value;

// Evento disparado quando a aplicação Spring termina de iniciar.
import org.springframework.boot.context.event.ApplicationReadyEvent;

// Permite criar um código que será executado quando um evento acontecer.
import org.springframework.context.ApplicationListener;

// Avisa ao Spring que o objeto devolvido pelo método deve ser guardado
// e administrado por ele.
import org.springframework.context.annotation.Bean;

// Avisa ao Spring que esta classe contém configurações.
import org.springframework.context.annotation.Configuration;


// Diz ao Spring:
// "Leia esta classe porque ela contém configurações do RabbitMQ".
@Configuration
public class RabbitMQConfig {


    /*
     * Busca no application.properties o valor desta propriedade:
     *
     * rabbitmq.exchange.name=nome-da-exchange
     *
     * Depois coloca esse valor dentro da variável exchangeName.
     *
     * Exemplo:
     *
     * application.properties:
     * rabbitmq.exchange.name=pedidos-exchange
     *
     * Resultado:
     * exchangeName terá o valor "pedidos-exchange".
     */
    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;


    /*
     * MÉTODO: pedidosExchange
     *
     * O que ele faz:
     * Cria a Exchange que receberá as mensagens de pedidos.
     *
     * A Exchange funciona como uma central de distribuição:
     *
     * Aplicação envia a mensagem
     *              ↓
     *           Exchange
     *              ↓
     *    envia para as filas conectadas
     *
     * Como estamos usando FanoutExchange, a mensagem será enviada
     * para todas as filas que estiverem ligadas a essa Exchange.
     */
    @Bean
    public Exchange pedidosExchange() {

        /*
         * Cria uma nova Exchange do tipo Fanout.
         *
         * O nome usado será o valor que veio do application.properties
         * e foi colocado na variável exchangeName.
         *
         * Exemplo:
         *
         * new FanoutExchange("pedidos-exchange")
         */
        return new FanoutExchange(exchangeName);
    }


    
    
    /*
     * MÉTODO: rabbitAdmin
     *
     * O que ele faz:
     * Cria um objeto responsável por administrar o RabbitMQ.
     *
     * O RabbitAdmin pode criar no RabbitMQ:
     *
     * - Exchanges
     * - filas
     * - ligações entre Exchanges e filas
     *
     * O parâmetro connectionFactory é entregue automaticamente pelo Spring.
     *
     * O Spring cria essa conexão usando:
     *
     * spring.rabbitmq.host
     * spring.rabbitmq.port
     * spring.rabbitmq.username
     * spring.rabbitmq.password
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {

        /*
         * Cria o RabbitAdmin usando a conexão com o RabbitMQ.
         *
         * É como dizer:
         *
         * "RabbitAdmin, use esta conexão para administrar
         * o servidor RabbitMQ."
         */
        return new RabbitAdmin(connectionFactory);
    }


    /*
     * MÉTODO: messageConverter
     *
     * O que ele faz:
     * Cria um conversor para transformar objetos Java em JSON.
     *
     * Exemplo:
     *
     * Objeto Java:
     *
     * Pedido pedido
     *
     * Pode ser transformado em algo parecido com:
     *
     * {
     *   "cliente": "Fulano",
     *   "valorTotal": 61.0
     * }
     *
     * Isso facilita o envio do objeto pelo RabbitMQ.
     */
    @Bean
    public MessageConverter messageConverter() {

        /*
         * Cria um conversor que usa o Jackson.
         *
         * Jackson é a biblioteca usada pelo Spring para trabalhar
         * com objetos Java e JSON.
         */
        return new Jackson2JsonMessageConverter();
    }


    /*
     * MÉTODO: rabbitTemplate
     *
     * O que ele faz:
     * Cria e configura o objeto que será usado para enviar mensagens.
     *
     * O RabbitTemplate é parecido com uma ferramenta de envio.
     *
     * Mais tarde, poderemos usar algo semelhante a:
     *
     * rabbitTemplate.convertAndSend(...)
     *
     * Este método recebe dois objetos que o Spring entrega automaticamente:
     *
     * connectionFactory:
     * conexão com o RabbitMQ.
     *
     * converter:
     * conversor JSON criado no método messageConverter().
     */
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter converter) {

        /*
         * Cria um RabbitTemplate usando a conexão com o RabbitMQ.
         *
         * Neste momento ele já sabe como chegar ao servidor RabbitMQ.
         */
        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        /*
         * Coloca o conversor JSON dentro do RabbitTemplate.
         *
         * Isso significa:
         *
         * "Quando eu enviar um objeto Java, transforme esse objeto
         * em JSON antes de mandar para o RabbitMQ."
         */
        rabbitTemplate.setMessageConverter(converter);

        /*
         * Devolve o RabbitTemplate configurado.
         *
         * Como o método tem @Bean, o Spring guardará esse objeto
         * para ser usado em outras classes.
         */
        return rabbitTemplate;
    }


    /*
     * MÉTODO: applicationListener
     *
     * O que ele faz:
     * Cria um código que será executado quando a aplicação terminar
     * completamente de iniciar.
     *
     * ApplicationReadyEvent significa:
     *
     * "A aplicação Spring terminou de subir e está pronta."
     *
     * O RabbitAdmin é entregue automaticamente pelo Spring.
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> applicationListener(
            RabbitAdmin rabbitAdmin) {

        /*
         * Esta seria a forma completa de escrever o listener:
         *
         * return new ApplicationListener<ApplicationReadyEvent>() {
         *
         *     @Override
         *     public void onApplicationEvent(
         *             ApplicationReadyEvent event) {
         *
         *         rabbitAdmin.initialize();
         *     }
         * };
         *
         * Ela significa:
         *
         * "Quando acontecer o evento ApplicationReadyEvent,
         * execute rabbitAdmin.initialize()."
         */


        /*
         * Esta é a mesma lógica escrita como expressão lambda.
         *
         * event:
         * representa o evento informando que a aplicação ficou pronta.
         *
         * ->
         * pode ser lido como "quando isso acontecer, execute".
         *
         * rabbitAdmin.initialize():
         * manda o RabbitAdmin verificar e criar no RabbitMQ
         * os recursos configurados na aplicação.
         */
        return event -> rabbitAdmin.initialize();
    }
}

