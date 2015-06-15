package com.automata.expt.async.client

import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.InvocationCallback
import javax.ws.rs.client.WebTarget
import java.util.concurrent.Future

/**
 * Created by Amith Krishnan on 6/14/15.
 */
class AsyncClient {

    final WebTarget target = ClientBuilder.newClient().target("http://localhost:8080/async-experiments/")

    void sendWork(String url) {
        target
                .path("resource/work")
                .queryParam("url", url)
                .request()
                .async()
                .get(new InvocationCallback<String>() {
            @Override
            public void completed(String response) {
                println "Got work done - $response"
                System.exit(1)
            }

            @Override
            public void failed(Throwable throwable) {
                println "Work not done"
            }
        })
    }

    public static void main(String[] args) {
        AsyncClient asyncClient = new AsyncClient()
        asyncClient.sendWork("http://google.com")

    }
}
