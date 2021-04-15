package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.Server;
import inf112.isolasjonsteamet.roborally.network.ServerPacketListener;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Server} which uses TCP over Netty under the hood.
 */
public class NettyServerImpl extends Thread implements Server {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerImpl.class);

	private final String host;
	private final int port;
	private final ServerHandler serverHandler;

	private final CompletableFuture<Void> readySignal = new CompletableFuture<>();
	private Channel rootChannel;

	private final AtomicBoolean closing = new AtomicBoolean(false);

	/**
	 * Constucts a new netty server on the given host and port.
	 */
	public NettyServerImpl(String host, int port, String gameName) {
		super("NettyServerImpl");
		this.host = host;
		this.port = port;

		this.serverHandler = new ServerHandler(gameName);
	}

	public CompletableFuture<Void> ready() {
		return readySignal;
	}

	@Override
	public void run() {
		var bossGroup = new NioEventLoopGroup();
		var workerGroup = new NioEventLoopGroup();

		try {
			var b = new ServerBootstrap();
			var codecHandler = new PacketCodecHandler();

			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new IdleStateHandler(60, 60, 60), codecHandler, serverHandler);
						}
					})
					.option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);

			ChannelFuture f = b.bind(host, port).sync();
			rootChannel = f.channel();
			readySignal.complete(null);
			f.channel().closeFuture().sync();
			LOGGER.debug("Server closed");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			LOGGER.debug("Event loops shut down");
		}
	}

	@Override
	public void sendToAllPlayers(Server2ClientPacket packet) {
		if (!closing.get()) {
			LOGGER.trace("Server -> All: {}", packet);
			serverHandler.sendToAllPlayers(packet);
		}
	}

	@Override
	public void sendToPlayer(String player, Server2ClientPacket packet) {
		if (!closing.get()) {
			LOGGER.trace("Server -> {}: {}", player, packet);
			serverHandler.sendToPlayer(player, packet);
		}
	}

	@Override
	public void addListener(ServerPacketListener<?> listener) {
		if (!closing.get()) {
			serverHandler.addListener(listener);
		}
	}

	@Override
	public void removeListener(ServerPacketListener<?> listener) {
		if (!closing.get()) {
			serverHandler.removeListener(listener);
		}
	}

	@Override
	public CompletableFuture<Void> close(@Nullable String reason) {
		CompletableFuture<Void> promise = new CompletableFuture<>();

		rootChannel.closeFuture().addListener((ChannelFutureListener) future1 -> promise.complete(null));

		if (!closing.getAndSet(true)) {
			LOGGER.debug("Closing server");
			serverHandler.disconnectAll(reason).addListener((ChannelGroupFutureListener) future -> rootChannel.close());
		}
		return promise;
	}

	@Override
	public void kickPlayer(String player, String reason) {
		if (!closing.get()) {
			serverHandler.kickPlayer(player, reason);
		}
	}

	@Override
	public List<String> getPlayers() {
		return serverHandler.getPlayers();
	}
}
