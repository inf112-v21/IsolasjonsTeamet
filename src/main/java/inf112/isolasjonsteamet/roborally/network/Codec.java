package inf112.isolasjonsteamet.roborally.network;

import io.netty.buffer.ByteBuf;

/**
 * Describes how a message can be encoded to bytes, and decoded from bytes.
 *
 * @param <A> The message to encode and decode.
 */
public interface Codec<A> {

	/** Decode the bytes from the supplied byte buffer, into the message representation. */
	A read(ByteBuf in);

	/** Encode the supplied message into bytes using the supplied byte buffer. */
	void write(A msg, ByteBuf buf);
}
