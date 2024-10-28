package hst;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;

/**
 *
 * @author vic
 */
public class Srv {

    private static final byte[] HELLO = {'H', 'e', 'l', 'l', 'o'};

    public static void main(final String[] args) throws Exception {
        final var srv = new Server(new InetSocketAddress("::1", 8081));
        srv.setDefaultHandler(new Handler.Wrapper() {

            @Override
            public boolean handle(final Request request, final Response response, final Callback callback) throws Exception {
                response.setStatus(200);
                response.getHeaders().add("content-type", "text/plain");
                response.write(true, ByteBuffer.wrap(HELLO), callback);
                return true;
            }
        });
        srv.start();
        srv.join();
    }
}
