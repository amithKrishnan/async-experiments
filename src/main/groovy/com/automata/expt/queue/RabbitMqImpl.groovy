package com.automata.expt.queue

import com.rabbitmq.client.*

/**
 * Created by Amith Krishnan on 6/13/15.
 */
class RabbitMqImpl {

    String inputQueue
    String outputQueue
    String exchange
    Connection connection
    Channel channel
    QueueingConsumer consumer

    RabbitMqImpl(){
        inputQueue = "input-queue"
        outputQueue = "output-queue"
        exchange = "worker-exchange"
    }

    void configure() {
        ConnectionFactory factory = new ConnectionFactory()
        String uri = System.getenv("CLOUDAMQP_URL") ?: "amqp://guest:guest@localhost"
        factory.setUri(uri)
        factory.setRequestedHeartbeat(30)
        factory.setConnectionTimeout(30)
        connection = factory.newConnection()
        channel = connection.createChannel()

        channel.exchangeDeclare(exchange, "direct", true);

        channel.queueDeclare(inputQueue, false, false, false, null)
        channel.queueBind(inputQueue, exchange, "wrkrIn");

        channel.queueDeclare(outputQueue, false, false, false, null)
        channel.queueBind(outputQueue, exchange, "wrkrOut");
    }

    void listen() {
        consumer = new QueueingConsumer(channel)
        channel.basicConsume(inputQueue, true, consumer)
    }

    boolean sendMessage(String message){
        boolean success = true
        try {
            channel.basicPublish(exchange, "wrkrOut", null, message.getBytes())
        } catch (IOException e) {
            //TODO error handling
            success = false
        }
        return success
    }

    void close(){
        channel?.close()
        connection?.close()
    }

}
