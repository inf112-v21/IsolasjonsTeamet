package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Client} which uses TCP over Netty under the hood.
 */
public class NettyClientImpl extends Thread implements Client {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientImpl.class);

	private final String host;
	private final int port;
	private final String username;

	private final ClientHandler clientHandler = new ClientHandler();

	private final CompletableFuture<Void> readySignal = new CompletableFuture<>();
	private Channel channel;

	private final AtomicBoolean disconnecting = new AtomicBoolean(false);

	public NettyClientImpl(String host, int port, String username) {
		super("NettyClientImpl");
		this.host = host;
		this.port = port;
		this.username = username;
	}

	public CompletableFuture<Void> ready() {
		return readySignal;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void run() {
		var bossGroup = new NioEventLoopGroup();
		var workerGroup = new NioEventLoopGroup();

		try {
			var b = new Bootstrap();
			var codecHandler = new PacketCodecHandler();

			b.group(workerGroup)
					.channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(codecHandler, clientHandler);
						}
					});

			ChannelFuture f = b.connect(host, port).sync();
			channel = f.channel();
			readySignal.complete(null);
			f.channel().closeFuture().sync();
			LOGGER.debug("Client closed");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			LOGGER.debug("Event loops shut down");
		}
	}

	@Override
	public void sendToServer(Client2ServerPacket packet) {
		if (!disconnecting.get()) {
			LOGGER.trace("{} -> Server {}: ", username, packet);
			clientHandler.sendMessage(packet);
		}
	}

	@Override
	public void addListener(ClientPacketListener<?> listener) {
		if (!disconnecting.get()) {
			clientHandler.addListener(listener);
		}
	}

	@Override
	public void removeListener(ClientPacketListener<?> listener) {
		if (!disconnecting.get()) {
			clientHandler.removeListener(listener);
		}
	}

	@Override
	public CompletableFuture<Void> disconnect(@Nullable String reason) {
		CompletableFuture<Void> promise = new CompletableFuture<>();
		channel.closeFuture().addListener((ChannelFutureListener) future1 -> promise.complete(null));

		if (!disconnecting.getAndSet(true)) {
			LOGGER.debug("Disconnecting client");
			clientHandler.disconnect(reason);
		}

		return promise;

	}
}
