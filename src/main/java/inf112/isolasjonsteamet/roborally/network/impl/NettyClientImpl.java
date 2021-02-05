package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.Client;
import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.CompletableFuture;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NettyClientImpl extends Thread implements Client {

	private final String host;
	private final int port;
	private final ClientHandler clientHandler = new ClientHandler();

	public NettyClientImpl(String host, int port) {
		this.host = host;
		this.port = port;
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
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	@Override
	public void sendToServer(Client2ServerPacket packet) {
		clientHandler.sendMessage(packet);
	}

	@Override
	public void addListener(ClientPacketListener<?> listener) {
		clientHandler.addListener(listener);
	}

	@Override
	public void removeListener(ClientPacketListener<?> listener) {
		clientHandler.removeListener(listener);
	}

	@Override
	public CompletableFuture<Void> disconnect(@Nullable String reason) {
		ChannelFuture future = clientHandler.disconnect(reason);
		CompletableFuture<Void> promise = new CompletableFuture<>();

		future.addListener((ChannelFutureListener) future1 -> promise.complete(null));
		return promise;
	}
}
