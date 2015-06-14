package com.automata.expt.queue

/**
 * Created by Amith Krishnan on 6/13/15.
 */
class RabbitMqImpl implements QueueInterface{

    @Override
    void configure() {

    }

    @Override
    boolean listen() {
        return false
    }

    @Override
    String receive() {
        return null
    }

    @Override
    boolean transmit(String message) {
        return false
    }
}
