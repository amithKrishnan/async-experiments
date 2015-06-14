package com.automata.expt.queue

/**
 * Created by Amith Krishnan on 6/13/15.
 */
interface QueueInterface {

    void configure()
    boolean listen()
    String receive()
    boolean transmit(String message)

}
