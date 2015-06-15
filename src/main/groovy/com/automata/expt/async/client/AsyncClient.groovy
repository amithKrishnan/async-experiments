package com.automata.expt.async.client

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.InvocationCallback
import javax.ws.rs.client.WebTarget

/**
 * Created by Amith Krishnan on 6/14/15.
 */
class AsyncClient {

    final WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/async-experiments/")
    int count = 1

    void sendWork(String url) {
        target.path("resource/work")
                .queryParam("url", url)
                .request()
                .async()
                .get(new InvocationCallback<String>() {
            @Override
            public void completed(String response) {
                println "Got work done - $response"
                count++
            }

            @Override
            public void failed(Throwable throwable) {
                println "Work not done"
            }
        })
    }

    public static void main(String[] args) {
        AsyncClient asyncClient = new AsyncClient()
        10.times {
            asyncClient.sendWork("http://google.com")
        }
        //do other work till all responses have come back
        while (asyncClient.count < 10) {
            print "."
        }
        System.exit(1)
    }
}
