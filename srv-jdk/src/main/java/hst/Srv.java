package hst;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Srv {

    private static final byte[] HELLO = {'H', 'e', 'l', 'l', 'o'};

    public static void main(final String[] args) throws IOException {
        System.setProperty("sun.net.httpserver.nodelay", "true");

        final var srv = HttpServer.create();
        srv.setExecutor(Executors.newFixedThreadPool(20));
        srv.createContext("/", (HttpExchange exch) -> {
            try (exch) {
                exch.getResponseHeaders().add("content-type", "text/html");
                exch.sendResponseHeaders(200, 0);
                exch.getResponseBody().write(HELLO);
                exch.getResponseBody().close();

            } catch (final Exception ex) {
                System.err.println(ex);
            }
        });

        srv.bind(new InetSocketAddress("[::1]", 8081), 20);

        srv.start();

        System.out.println(srv.getAddress());
    }
}
