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

    static void main(String[] args){
        Worker worker = new Worker()
        worker.init()
        worker.start()
        worker.setupShutdownHook()
    }

    void init() {
        queue = new RabbitMqImpl()
        queue.configure()
        queue.workerListen()
        crawler = new Crawler()
    }

    void start(){
        new Thread(new Runnable(){
            void run(){
                while (true) {
                        QueueingConsumer.Delivery delivery = queue.workerConsumer.nextDelivery()
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
        work(receivedMessage)
    }

    void work(String processedMessage){
        String crawledOutput = crawler.crawl(processedMessage)
        Matcher titleMatcher = (crawledOutput =~ /<title>(.*?)<\/title>/)
        if(titleMatcher.find()){
            String title = titleMatcher[0][1]
            String outputMessage = prepareOutputMessage(title)
            queue.sendMessage(outputMessage, queue.outputQueueKey)
        }
    }

    String prepareOutputMessage(String crawledOutput){
        //TODO convert to JSON and logging
        println "preparing to send $crawledOutput"
        return crawledOutput
    }

    void setupShutdownHook() {
        addShutdownHook {
            println("Shutting down the worker")
            queue?.close()
        }
    }

}
