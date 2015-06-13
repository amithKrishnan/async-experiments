package com.automata.expt.queue

import spock.lang.Specification

/**
 * Created by Amith Krishnan on 6/12/15.
 */
class ReceiverSpec extends Specification {

    Receiver receiver

    void setup() {
        receiver = new Receiver()
    }

    def "processMessage extracts the url from the received message"(){
        setup:
        String receivedMessage = "someJsonObject"

        when:
        String extractedUrl = receiver.processMessage(receivedMessage)

        then:
        extractedUrl
    }

}
