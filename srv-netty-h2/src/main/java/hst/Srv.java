package hst;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.DefaultHttp2HeadersFrame;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2EventAdapter;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameStream;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2Stream;
import java.net.InetSocketAddress;

/**
 *
 * @author vic
 */
public class Srv {

    static final ByteBuf HELLO = Unpooled.unreleasableBuffer(
         Unpooled.copiedBuffer(new byte[] {'H', 'e', 'l', 'l', 'o'}));

    public static void main(final String[] args) throws InterruptedException {
        final var bossGroup = new NioEventLoopGroup(1);
        final var workerGroup = new NioEventLoopGroup();

        final var srv = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        System.out.println(ch);
                        final var connection = new DefaultHttp2Connection(true);
                        final var frameListener = new FrameListener(connection);
                        final var connectionHandler = new Http2ConnectionHandlerBuilder()
                                .connection(connection)
                                .frameListener(frameListener)
                                .build();

                        ch.pipeline()
                                .addLast(connectionHandler)
                                .addLast(new MyHandler(connectionHandler.encoder()));
                    }
                });

        srv.bind(new InetSocketAddress("::1", 8082))
                .sync()
                .channel()
                .closeFuture()
                .sync();
    }

    private static class MyHandler extends ChannelDuplexHandler {

        private final Http2ConnectionEncoder encoder;

        public MyHandler(final Http2ConnectionEncoder encoder) {
            this.encoder = encoder;
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            System.out.println(msg);
            if (msg instanceof Http2HeadersFrame headersFrame && headersFrame.isEndStream()) {
                final var headers = new DefaultHttp2Headers().status(OK.codeAsText());
                encoder.writeHeaders(ctx, headersFrame.stream().id(), headers, 0, false, ctx.newPromise());
                encoder.writeData(ctx, headersFrame.stream().id(), HELLO, 0, true, ctx.newPromise());
                ctx.flush();
            }
        }
    }

    private static class FrameListener extends Http2EventAdapter {

        private final Http2Connection connection;

        public FrameListener(final Http2Connection connection) {
            this.connection = connection;
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endStream) throws Http2Exception {
            final var stream = connection.stream(streamId);
            final Http2FrameStream frameStream;
            if (stream instanceof Http2FrameStream http2FrameStream) {
                frameStream = http2FrameStream;

            } else {
                frameStream = new Http2FrameStream() {
                    @Override
                    public int id() {
                        return stream.id();
                    }

                    @Override
                    public Http2Stream.State state() {
                        return stream.state();
                    }
                };
            }

            final var frame = new DefaultHttp2HeadersFrame(headers, endStream, padding)
                    .stream(frameStream);
            ctx.fireChannelRead(frame);
        }
    }
}
