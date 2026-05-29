package edu.nju.di.integration.server;

import com.sun.net.httpserver.HttpServer;
import edu.nju.di.integration.server.handler.CrossDropHandler;
import edu.nju.di.integration.server.handler.CrossSelectHandler;
import edu.nju.di.integration.server.handler.ShareHandler;
import edu.nju.di.integration.server.handler.StatisticsHandler;
import edu.nju.di.integration.xslt.XsltTransformer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class IntegrationServerMain {
    public static void main(String[] args) throws Exception {
        IntegrationConfig config = IntegrationConfig.load();

        HttpServer server = HttpServer.create(new InetSocketAddress(config.serverPort), 0);
        server.setExecutor(Executors.newFixedThreadPool(16));

        XsltTransformer transformer = new XsltTransformer(config.xsltDir);

        server.createContext("/share", new ShareHandler(config, transformer));
        server.createContext("/crossSelect", new CrossSelectHandler(config, transformer));
        server.createContext("/crossDrop", new CrossDropHandler(config, transformer));
        server.createContext("/statistics", new StatisticsHandler(config, transformer));

        server.start();
        System.out.println("[integration-server] listening on port " + config.serverPort);
        System.out.println("[integration-server] POST http://localhost:" + config.serverPort + "/share");
        System.out.println("[integration-server] POST http://localhost:" + config.serverPort + "/crossSelect");
        System.out.println("[integration-server] POST http://localhost:" + config.serverPort + "/crossDrop");
        System.out.println("[integration-server] GET  http://localhost:" + config.serverPort + "/statistics");
    }
}
