package socket;

        import io.netty.bootstrap.Bootstrap;
        import io.netty.buffer.Unpooled;
        import io.netty.channel.*;
        import io.netty.channel.nio.NioEventLoopGroup;
        import io.netty.channel.socket.SocketChannel;
        import io.netty.channel.socket.nio.NioSocketChannel;
        import io.netty.handler.codec.http.DefaultHttpHeaders;
        import io.netty.handler.codec.http.HttpClientCodec;
        import io.netty.handler.codec.http.HttpObjectAggregator;
        import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
        import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
        import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
        import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
        import io.netty.handler.codec.http.websocketx.WebSocketFrame;
        import io.netty.handler.codec.http.websocketx.WebSocketVersion;
        import io.netty.handler.ssl.SslContext;
        import io.netty.handler.ssl.SslContextBuilder;
        import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
        import io.netty.handler.ssl.util.SelfSignedCertificate;
        import rx.schedulers.Schedulers;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.net.URI;

/**
 * This is an example of a WebSocket client.
 * <p>
 * In order to run this example you need a compatible WebSocket server.
 * Therefore you can either start the WebSocket server from the examples
 * by running {@link io.netty.example.http.websocketx.server.WebSocketServer}
 * or connect to an existing WebSocket server such as
 * <a href="http://www.websocket.org/echo.html">ws://echo.websocket.org</a>.
 * <p>
 * The client will attempt to connect to the URI passed to it as the first argument.
 * You don't have to specify any arguments if you want to connect to the example WebSocket server,
 * as this is the default.
 */
public final class WebSocketClient {

    static final String URL = System.getProperty("url", "ws://127.0.0.1:8001/websocket");

    private Channel c;

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme();
        final String host = uri.getHost();
        final int port = uri.getPort();

        final boolean ssl = "wss".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Connect with V13 (RFC 6455 aka HyBi-17). You can change it to V08 or V00.
        // If you change it to V00, ping is not supported and remember to change
        // HttpResponseDecoder to WebSocketHttpResponseDecoder in the pipeline.
        final WebSocketClientHandler handler =
                new WebSocketClientHandler(
                        WebSocketClientHandshakerFactory.newHandshaker(
                                uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                        }
                        p.addLast(
                                new HttpClientCodec(),
                                new HttpObjectAggregator(8192),
                                handler);
                    }
                });

        Channel ch = b.connect(uri.getHost(), port).channel();
        ChannelFuture f = handler.handshakeFuture();


        handler.observe().observeOn(Schedulers.newThread()).subscribe(message -> {
            System.out.printf("I'm the first subscriber: %s\n", message);
        });

        handler.observe().observeOn(Schedulers.newThread()).subscribe(message -> {
            System.out.printf("I'm the second subscriber: %s\n", message);
        });


        for(int i = 0; i < 5; i++) {
            Thread.sleep(1000);
        }

        handler.observe().observeOn(Schedulers.newThread()).subscribe(message -> {
            System.out.printf("I'm the third subscriber: %s\n", message);
        });

    }
}
