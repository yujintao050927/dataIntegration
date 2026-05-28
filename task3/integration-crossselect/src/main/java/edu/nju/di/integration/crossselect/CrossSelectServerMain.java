package edu.nju.di.integration.crossselect;

import com.sun.net.httpserver.HttpServer;
import edu.nju.di.integration.xslt.XsltTransformer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class CrossSelectServerMain {
    public static void main(String[] args) throws Exception {
        CrossSelectConfig config = CrossSelectConfig.load();

        HttpServer server = HttpServer.create(new InetSocketAddress(config.serverPort), 0);
        server.setExecutor(Executors.newFixedThreadPool(8));

        XsltTransformer transformer = new XsltTransformer(config.xsltDir);
        server.createContext("/crossSelect", new CrossSelectHandler(config, transformer));

        server.start();
        System.out.println("[integration-crossselect] listening on port " + config.serverPort);
        System.out.println("[integration-crossselect] POST http://localhost:" + config.serverPort + "/crossSelect");
    }
}
