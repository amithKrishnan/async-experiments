package com.automata.expt.async.server

import com.automata.expt.queue.RabbitMqImpl
import com.rabbitmq.client.QueueingConsumer
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended

/**
 * Created by Amith Krishnan on 6/14/15.
 */
@Path("/resource")
class Application {

    final URI BASE_URI = URI.create("http://localhost:8080/async-experiments/")
    ResourceConfig resourceConfig
    HttpServer server

    static void main(String[] args) {
        Application application = new Application()
        application.configureResources()
        application.startServer()
        application.setupShutdownHook()
    }

    void startServer() {
        try {
            server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, resourceConfig)
        } catch (IOException ex) {
            println("Error starting server")
            ex.printStackTrace()
        }
    }

    void configureResources() {
        resourceConfig = new ResourceConfig().registerClasses(Application)
    }

    void setupShutdownHook() {
        addShutdownHook {
            println("Shutting down the server")
            server?.shutdownNow()
        }
    }

    @GET
    @Path("/tester")
    String tester(@QueryParam("url") final String url) {
        println "tester here"
        return url
    }

    @GET
    @Path("/work")
    void work(@Suspended final AsyncResponse asyncResponse, @QueryParam("url") final String url) {

        new Thread(new Runnable() {
            RabbitMqImpl queue

            @Override
            void run() {
                println "Starting thread @ ${new Date().toString()}"
                String result = ""
                queue = new RabbitMqImpl()
                queue.configure()
                if(queue.sendMessage(url,queue.inputQueueKey)){
                    queue.appListen()
                    while (true) {
                        QueueingConsumer.Delivery delivery = queue.appConsumer.nextDelivery()
                        if(delivery){
                            result = new String(delivery.getBody())
                            println "Service got $result"
                            queue?.close()
                            break;
                        }else{
                            println "Waiting"
                        }
                    }
                }
                asyncResponse.resume(result)
            }
        }, "Thread @ ${new Date().toString()}").start()
    }

}
