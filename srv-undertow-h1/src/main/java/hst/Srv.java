package hst;

import io.undertow.Undertow;
import io.undertow.util.HttpString;
import java.nio.ByteBuffer;

/**
 *
 * @author vic
 */
public class Srv {

    private static final byte[] HELLO = {'H', 'e', 'l', 'l', 'o'};
    private static final HttpString CONTENT_LENGTH = new HttpString("content-length");
    private static final HttpString CONTENT_TYPE = new HttpString("content-type");

    public static void main(final String[] args) {
        final var srv = Undertow.builder()
                .addHttpListener(8081, "::1")
                .setHandler((ex) -> {
                    ex.setStatusCode(200);
                    ex.getResponseHeaders()
                            .add(CONTENT_TYPE, "text/plain")
                            .add(CONTENT_LENGTH, HELLO.length);
                    ex.getResponseSender()
                            .send(ByteBuffer.wrap(HELLO));
                })
                .build();

        srv.start();
    }
}
