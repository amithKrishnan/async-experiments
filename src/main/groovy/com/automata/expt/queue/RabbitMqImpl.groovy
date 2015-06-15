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
    QueueingConsumer workerConsumer
    QueueingConsumer appConsumer
    String inputQueueKey
    String outputQueueKey

    RabbitMqImpl(){
        inputQueue = "input-queue"
        outputQueue = "output-queue"
        exchange = "worker-exchange"
        inputQueueKey = "wrkrIn"
        outputQueueKey = "wrkrOut"
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
        channel.queueBind(inputQueue, exchange, inputQueueKey);

        channel.queueDeclare(outputQueue, false, false, false, null)
        channel.queueBind(outputQueue, exchange, outputQueueKey);
    }

    void workerListen() {
        workerConsumer = new QueueingConsumer(channel)
        channel.basicConsume(inputQueue, true, workerConsumer)
    }

    void appListen() {
        appConsumer = new QueueingConsumer(channel)
        channel.basicConsume(outputQueue, true, appConsumer)
    }

    boolean sendMessage(String message, String routingKey){
        boolean success = true
        try {
            channel.basicPublish(exchange, routingKey, null, message.getBytes())
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
