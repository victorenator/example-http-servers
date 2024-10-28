package hst;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;

/**
 *
 * @author vic
 */
public class Srv {

    private static final byte[] HELLO = {'H', 'e', 'l', 'l', 'o'};
    private static final HttpString CONTENT_TYPE = new HttpString("content-type");

    public static void main(final String[] args) {
        final var srv = Undertow.builder()
                .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .addHttpListener(8082, "::1")
                .setHandler((ex) -> {
                    ex.setStatusCode(200);
                    ex.getResponseHeaders()
                            .add(CONTENT_TYPE, "text/plain");
                    ex.getResponseSender()
                            .send(ByteBuffer.wrap(HELLO));
                })
                .build();

        srv.start();
    }
}
