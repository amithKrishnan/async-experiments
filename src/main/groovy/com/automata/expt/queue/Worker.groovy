package com.automata.expt.queue

import com.automata.expt.crawl.Crawler
import com.rabbitmq.client.QueueingConsumer

import java.util.regex.Matcher

/**
 * Created by Amith Krishnan on 6/12/15.
 */
class Worker {

    RabbitMqImpl queue
    Crawler crawler

    static void main(String[] args) {
        Worker worker = new Worker()
        worker.init()
        worker.start()
    }

    void init() {
        queue = new RabbitMqImpl()
        queue.configure()
        queue.listen()
        crawler = new Crawler()
    }

    void start(){
        new Thread(new Runnable(){
            void run(){
                while (true) {
                        QueueingConsumer.Delivery delivery = queue.consumer.nextDelivery()
                        String message = new String(delivery.getBody())
                        processMessage(message)
                }
            }
        }).start()
        //TODO log instead of println
        println("Starting message consumption")
    }

    void processMessage(String receivedMessage){
        //TODO convert from json and extract required and logging
        println "got $receivedMessage"
        receivedMessage = "http://"+receivedMessage
        work(receivedMessage)
    }

    void work(String processedMessage){
        String crawledOutput = crawler.crawl(processedMessage)
        Matcher titleMatcher = (crawledOutput =~ /<title>(.*?)<\/title>/)
        if(titleMatcher.find()){
            String title = titleMatcher[0][1]
            String outputMessage = prepareOutputMessage(title)
            queue.sendMessage(outputMessage)
        }
    }

    String prepareOutputMessage(String crawledOutput){
        //TODO convert to JSON and logging
        println "preparing to send $crawledOutput"
        return crawledOutput
    }

}
