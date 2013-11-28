/**
 *  @author Jose Luis Montes Jimenez (@spCoder)
 *  
 */
package com.ef.ctx.rabbitmq.listener;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.fusesource.jansi.AnsiConsole;

import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RabbitListener {

    private static final String ANSI_NORMAL = "\u001b[0m";
    // private static final String ANSI_BOLD = "\u001b[1m";
    private static final String ANSI_RED = "\u001b[31m";
    private static final String ANSI_BLUE = "\u001b[34m";
    private static final String ANSI_GREEN = "\u001b[32m";

    private static final int HOST = 0;
    private static final int PORT = 1;
    private static final int USERNAME = 2;
    private static final int PASSWORD = 3;
    private static final int VIRTUALHOST = 4;
    private static final int EXCHANGENAME = 5;
    private static final int ROUTINGKEY = 6;

    private static ConnectionFactory connectionFactory = null;

    private static SimpleDateFormat dateParser = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");

    public static void main(String[] argv) throws Exception {

        if (!paramsOk(argv)) {
            return;
        }

        printParams(argv);

        ConnectionFactory factory = getConnectionFactory(argv);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(argv[EXCHANGENAME], "topic");

        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, argv[EXCHANGENAME], argv[ROUTINGKEY]);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            BasicProperties props = delivery.getProperties();

            // System.out.println(getTime() + " [RoutingKey= " + delivery.getEnvelope().getRoutingKey() + " , MessageType= " + props.getType() + "] : '"
            // + message + "'");
            AnsiConsole.out.print(getTime());
            AnsiConsole.out.print(" [RoutingKey=");
            AnsiConsole.out.print(ANSI_RED + delivery.getEnvelope().getRoutingKey() + ANSI_NORMAL);
            AnsiConsole.out.print(", MessageType=");
            AnsiConsole.out.print(ANSI_BLUE + props.getType() + ANSI_NORMAL);
            AnsiConsole.out.print("] : '");
            AnsiConsole.out.print(ANSI_GREEN + message + ANSI_NORMAL);
            AnsiConsole.out.println("'");
            System.out.println();
        }
    }

    private static String getTime() {
        Date date = new Date();
        return dateParser.format(date);
    }

    private static ConnectionFactory getConnectionFactory(String[] argv) {

        if (connectionFactory == null) {
            System.out.println("initialising connection");
            connectionFactory = new ConnectionFactory();

            connectionFactory.setRequestedHeartbeat(30);

            connectionFactory.setHost(argv[HOST]);
            connectionFactory.setPort(Integer.parseInt(argv[PORT]));
            connectionFactory.setUsername(argv[USERNAME]);
            connectionFactory.setPassword(argv[PASSWORD]);
            connectionFactory.setVirtualHost(argv[VIRTUALHOST]);
        }

        return connectionFactory;
    }

    private static void printParams(String[] argv) {
        AnsiConsole.out.println(ANSI_BLUE
                + "############################################################################################################");

        AnsiConsole.out.println("HOST: " + argv[HOST]);
        AnsiConsole.out.println("PORT: " + argv[PORT]);
        AnsiConsole.out.println("USERNAME: " + argv[USERNAME]);
        AnsiConsole.out.println("PASSWORD: " + argv[PASSWORD]);
        AnsiConsole.out.println("VIRTUALHOST: " + argv[VIRTUALHOST]);
        AnsiConsole.out.println("EXCHANGENAME: " + argv[EXCHANGENAME]);
        AnsiConsole.out.println("ROUTINGKEY: " + argv[ROUTINGKEY]);

        AnsiConsole.out.println("############################################################################################################"
                + ANSI_NORMAL);
    }

    private static boolean paramsOk(String[] argv) {
        if (argv.length != 7) {

            System.out.println("# PARAM ORDER");
            System.out.println("# HOST = 0");
            System.out.println("# PORT = 1");
            System.out.println("# USERNAME = 2");
            System.out.println("# PASSWORD = 3");
            System.out.println("# VIRTUALHOST = 4");
            System.out.println("# EXCHANGENAME = 5");
            System.out.println("# ROUTINGKEY = 6");

            return false;
        }
        return true;
    }
}