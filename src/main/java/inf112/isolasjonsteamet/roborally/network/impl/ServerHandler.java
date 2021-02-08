package inf112.isolasjonsteamet.roborally.network.impl;

import com.google.common.collect.ImmutableList;
import inf112.isolasjonsteamet.roborally.network.Packet;
import inf112.isolasjonsteamet.roborally.network.ServerPacketListener;
import inf112.isolasjonsteamet.roborally.network.c2spackets.Client2ServerPacket;
import inf112.isolasjonsteamet.roborally.network.c2spackets.GameJoinPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameInfoPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.GameJoinResultPacket.JoinResult;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.Server2ClientPacket;
import inf112.isolasjonsteamet.roborally.network.s2cpackets.ServerClosingPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerHandler.class);

	private final List<ServerPacketListener<?>> listeners = new CopyOnWriteArrayList<>();
	private final String gameName;

	private final ChannelGroup gamePlayers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	private final Map<String, Channel> playersToChannel = new ConcurrentHashMap<>();
	private final Map<Channel, String> channelToPlayers = new ConcurrentHashMap<>();

	public ServerHandler(String gameName) {
		this.gameName = gameName;
	}

	private void sendPacket(ChannelHandlerContext ctx, Server2ClientPacket packet) {
		ctx.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public ChannelGroupFuture sendToAllPlayers(Server2ClientPacket packet) {
		return gamePlayers.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void sendToPlayer(String player, Server2ClientPacket packet) {
		playersToChannel.get(player).writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void addListener(ServerPacketListener<?> listener) {
		listeners.add(listener);
	}

	public void removeListener(ServerPacketListener<?> listener) {
		listeners.remove(listener);
	}

	public ChannelGroupFuture disconnectAll(@Nullable String reason) {
		sendToAllPlayers(new ServerClosingPacket(reason))
				.addListener((ChannelGroupFutureListener) future -> future.group().close());
		return gamePlayers.newCloseFuture();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		var players = ImmutableList.copyOf(playersToChannel.keySet());
		sendPacket(ctx, new GameInfoPacket(PacketProtocol.PROTOCOL, PacketProtocol.REQUIRED_VERSION, gameName, players));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String player = channelToPlayers.remove(ctx.channel());
		playersToChannel.remove(player);
	}

	private void handleGameJoin(ChannelHandlerContext ctx, GameJoinPacket packet) {
		Channel channel = ctx.channel();
		String playerName = packet.getPlayerName();

		//For the moment we'll be lenient in cases like this with double registration
		if (channelToPlayers.containsKey(channel) && channelToPlayers.get(channel).equals(playerName)) {
			return;
		}

		JoinResult result;
		if (!playersToChannel.containsKey(playerName)) {
			gamePlayers.add(channel);
			playersToChannel.put(playerName, channel);
			channelToPlayers.put(channel, playerName);
			result = JoinResult.SUCCESS;
		} else {
			result = JoinResult.NAME_IN_USE;
		}

		sendPacket(ctx, new GameJoinResultPacket(result));
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
		if (!(msg instanceof Client2ServerPacket)) {
			LOGGER.warn("Server received client message " + msg);
			return;
		}

		//We handle this one extra specially here first
		if (msg instanceof GameJoinPacket) {
			handleGameJoin(ctx, (GameJoinPacket) msg);
		}

		listeners.forEach(listener ->
				listener.handleIfPossible(channelToPlayers.get(ctx.channel()), (Client2ServerPacket) msg));
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent && !gamePlayers.contains(ctx.channel())) {
			ctx.close();
		}
	}
}