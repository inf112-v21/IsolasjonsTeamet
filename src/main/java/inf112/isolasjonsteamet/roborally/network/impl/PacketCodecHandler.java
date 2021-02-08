package inf112.isolasjonsteamet.roborally.network.impl;

import inf112.isolasjonsteamet.roborally.network.Codec;
import inf112.isolasjonsteamet.roborally.network.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.HashMap;
import java.util.Map;

@Sharable
public class PacketCodecHandler extends ChannelDuplexHandler {

	public final Map<Class<?>, Byte> discriminatorMap = new HashMap<>();
	public final Map<Byte, Codec<?>> codecMap = new HashMap<>();

	private static final byte[] LENGTH_PLACEHOLDER = new byte[3];

	private final MessageToByteEncoder<? extends Packet> encoder = new MessageToByteEncoder<>() {

		@SuppressWarnings("unchecked")
		@Override
		protected void encode(ChannelHandlerContext ctx, Packet msg, ByteBuf out) throws Exception {
			if (!discriminatorMap.containsKey(msg.getClass())) {
				throw new IllegalStateException("Tried to send unregistered packet " + msg.getClass());
			}

			byte discriminator = discriminatorMap.get(msg.getClass());
			final Codec<Packet> codec = (Codec<Packet>) codecMap.get(discriminator);

			final int startIdx = out.writerIndex();
			out.writeBytes(LENGTH_PLACEHOLDER);
			out.writeByte(discriminator);
			codec.write(msg, out);

			int size = out.writerIndex() - startIdx - LENGTH_PLACEHOLDER.length;
			out.setMedium(startIdx, size);
		}
	};

	//Max size 64kb
	private final LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(
			64 * 1024, 0, LENGTH_PLACEHOLDER.length, 0, LENGTH_PLACEHOLDER.length
	) {
		@Override
		protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
			ByteBuf buf = (ByteBuf) super.decode(ctx, in);
			byte discriminator = buf.readByte();
			if (!codecMap.containsKey(discriminator)) {
				throw new IllegalStateException("Tried to decode unknown packet " + discriminator);
			}

			final Codec<?> codec = codecMap.get(discriminator);
			return codec.read(buf);
		}
	};

	public PacketCodecHandler() {
		//noinspection ConstantConditions
		if (PacketProtocol.REGISTRATIONS.length > 255) {
			throw new IllegalArgumentException("Too many packet registrations");
		}

		byte discriminator = Byte.MIN_VALUE;
		for (PacketRegistration<?> registration : PacketProtocol.REGISTRATIONS) {
			discriminatorMap.put(registration.getClazz(), discriminator);
			codecMap.put(discriminator, registration.getCodec());
			discriminator++;
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		decoder.channelRead(ctx, msg);
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		encoder.write(ctx, msg, promise);
	}
}
