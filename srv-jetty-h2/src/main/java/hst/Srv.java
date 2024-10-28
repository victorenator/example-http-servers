package hst;

import static java.lang.System.Logger.Level.INFO;
import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpFields;
import static org.eclipse.jetty.http.HttpHeader.CONTENT_TYPE;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.eclipse.jetty.http.HttpVersion.HTTP_2;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.server.RawHTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.Callback;

/**
 *
 * @author vic
 */
public class Srv {

    private static final System.Logger L = System.getLogger("HTTP2");

    private static final byte[] HELLO = {'H', 'e', 'l', 'l', 'o'};


    public static void main(final String[] args) throws Exception {
        final var srv = new Server();
        final var sessionListener = new ServerSessionListener() {

            @Override
            public Stream.Listener onNewStream(final Stream stream, final HeadersFrame frame) {
                System.out.println(stream);
                System.out.println(frame);
                final var headers = HttpFields.build()
                        .add(CONTENT_TYPE, "text/plain")
                        .asImmutable();
                final var res = new MetaData.Response(OK_200, null, HTTP_2, headers);
                stream.headers(new HeadersFrame(res, null, false), Callback.NOOP);
                stream.data(new DataFrame(ByteBuffer.wrap(HELLO), true), Callback.NOOP);
                return null;
            }
        };
        final var http2 = new RawHTTP2ServerConnectionFactory(sessionListener);
        http2.setMaxConcurrentStreams(128);
        http2.setConnectProtocolEnabled(false);
        final var connector = new ServerConnector(srv, http2);
        connector.setHost("::1");
        connector.setPort(8082);
        System.out.println(connector);
        srv.addConnector(connector);

        srv.start();
        srv.join();
    }
}
