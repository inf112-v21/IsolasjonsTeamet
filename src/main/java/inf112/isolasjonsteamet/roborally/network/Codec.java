package inf112.isolasjonsteamet.roborally.network;

import io.netty.buffer.ByteBuf;

public interface Codec<A> {

	A read(ByteBuf in);

	void write(A msg, ByteBuf buf);
}
