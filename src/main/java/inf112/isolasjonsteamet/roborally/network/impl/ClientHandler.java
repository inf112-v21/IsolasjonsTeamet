package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.ClientPacketListener;
import inf112.isolasjonsteamet.roborally.network.Packet;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.ClientDisconnectingPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client handler netty pipeline step. Sends packets received from the server onto listeners.
 */
@Sharable
public class ClientHandler extends SimpleChannelInboundHandler<Packet> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientHandler.class);

	private final List<ClientPacketListener<?>> listeners = new CopyOnWriteArrayList<>();

	private ChannelHandlerContext ctx;

	public ChannelFuture sendMessage(Client2ServerPacket packet) {
		return ctx.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void addListener(ClientPacketListener<?> listener) {
		listeners.add(listener);
	}

	public void removeListener(ClientPacketListener<?> listener) {
		listeners.remove(listener);
	}

	public ChannelFuture disconnect(@Nullable String reason) {
		sendMessage(new ClientDisconnectingPacket(reason)).addListener(ChannelFutureListener.CLOSE);
		return ctx.channel().closeFuture();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.ctx = ctx;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		if (!(msg instanceof Server2ClientPacket)) {
			LOGGER.warn("Client received server message " + msg);
			return;
		}

		listeners.forEach(listener -> listener.handleIfPossible((Server2ClientPacket) msg));
	}
}
