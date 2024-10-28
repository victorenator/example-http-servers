package hst;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.codec.http.LastHttpContent;
import java.net.InetSocketAddress;

/**
 *
 * @author vic
 */
public class Srv {

    private static final byte[] HELLO = {'H', 'e', 'l', 'l', 'o'};

    public static void main(final String[] args) throws InterruptedException {
        final var bossGroup = new NioEventLoopGroup(1);
        final var workerGroup = new NioEventLoopGroup();

        final var srv = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    final ChannelHandler handler = new MyHandler();

                    @Override
                    protected void initChannel(final SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new HttpRequestDecoder())
                                .addLast(new HttpResponseEncoder())
                                .addLast(handler);
                    }
                });

        srv.bind(new InetSocketAddress("::1", 8081))
                .sync()
                .channel()
                .closeFuture()
                .sync();
    }

    @Sharable
    private static class MyHandler extends SimpleChannelInboundHandler {

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            if (msg instanceof LastHttpContent) {
                final var res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(HELLO));
                res.headers().add("content-type", "text/plain");
                res.headers().add("content-length", HELLO.length);
                ctx.writeAndFlush(res);
            }
        }
    };
}
