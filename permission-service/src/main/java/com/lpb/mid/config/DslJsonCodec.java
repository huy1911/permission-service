package com.lpb.mid.config;

import com.dslplatform.json.JsonReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class DslJsonCodec<T> extends BaseCodec {
    private final JsonReader.ReadObject<T> reader;
    private final Charset charset;
    private final Encoder encoder = in -> {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        ByteBufOutputStream os = new ByteBufOutputStream(buffer);
        DslJsonUtils.encode(in, os);
        return os.buffer();
    };

    private final Decoder<T> decoder = new Decoder<T>() {
        @Override
        public T decode(ByteBuf buf, State state) {
            return DslJsonUtils.decode(new ByteBufInputStream(buf), reader);
        }
    };
    private final Encoder stringEncoder = new Encoder() {
        @Override
        public ByteBuf encode(Object in) throws IOException {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            out.writeCharSequence(in.toString(), charset);
            return out;
        }
    };

    private final Decoder<Object> stringDecoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) {
            String str = buf.toString(charset);
            buf.readerIndex(buf.readableBytes());
            return str;
        }
    };
    public DslJsonCodec(JsonReader.ReadObject<T> reader) {
        this.reader = reader;
        this.charset = StandardCharsets.UTF_8;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return (Decoder<Object>) decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder<Object> getMapValueDecoder() {
        return (Decoder<Object>) decoder;
    }

    @Override
    public Encoder getMapValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder<Object> getMapKeyDecoder() {
        return stringDecoder;
    }

    @Override
    public Encoder getMapKeyEncoder() {
        return stringEncoder;
    }
}
