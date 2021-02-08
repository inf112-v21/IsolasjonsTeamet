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
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An implementation of {@link Server} which uses TCP over Netty under the hood.
 */
public class NettyServerImpl extends Thread implements Server {

	private final String host;
	private final int port;
	private final String gameName;
	private final ServerHandler serverHandler;

	private final CompletableFuture<Void> readySignal = new CompletableFuture<>();
	private Channel rootChannel;

	/**
	 * Constucts a new netty server on the given host and port.
	 */
	public NettyServerImpl(String host, int port, String gameName) {
		this.host = host;
		this.port = port;
		this.gameName = gameName;

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
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	@Override
	public void sendToAllPlayers(Server2ClientPacket packet) {
		serverHandler.sendToAllPlayers(packet);
	}

	@Override
	public void sendToPlayer(String player, Server2ClientPacket packet) {
		serverHandler.sendToPlayer(player, packet);
	}

	@Override
	public void addListener(ServerPacketListener<?> listener) {
		serverHandler.addListener(listener);
	}

	@Override
	public void removeListener(ServerPacketListener<?> listener) {
		serverHandler.removeListener(listener);
	}

	@Override
	public CompletableFuture<Void> close(@Nullable String reason) {
		serverHandler.disconnectAll(reason).addListener((ChannelGroupFutureListener) future -> rootChannel.close());
		CompletableFuture<Void> promise = new CompletableFuture<>();

		rootChannel.closeFuture().addListener((ChannelFutureListener) future1 -> promise.complete(null));
		return promise;
	}
}
