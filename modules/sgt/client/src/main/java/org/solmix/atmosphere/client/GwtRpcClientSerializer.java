package org.solmix.atmosphere.client;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.Serializer;

/**
 * The base class for serializers. To instantiate this class follow this example:
 * <pre><code>
 * <p/>
 * {@literal @SerialTypes({ MyType1.class, MyType2.class })}
 * public abstract class MyCometSerializer extends AtmosphereGWTSerializer {}
 * <p/>
 * AtmosphereGWTSerializer serializer = GWT.create(MyCometSerializer.class);
 * AtmosphereClient client = new AtmosphereClient(url, serializer, listener);
 * </code></pre>
 * <p/>
 * Where MyType1 and MyType2 are the types that your expecting to receive from the server.
 * If you have a class hierarchy of messages that you want to send you only need to supply the base class here.
 * <p/>
 * For instance:
 * <pre><code>
 * public class Message {}
 * <p/>
 * public class MessageA extends Message {}
 * <p/>
 * public class MessageB extends Message {}
 * <p/>
 * {@literal @SerialTypes( Message.class )}
 * public abstract class MyCometSerializer extends AtmosphereGWTSerializer {}
 * <p/>
 * </code></pre>
 */
public abstract class GwtRpcClientSerializer implements ClientSerializer {

    private static final Logger logger = Logger.getLogger(GwtRpcClientSerializer.class.getName());

    // buffer in order to capture split messages
    private final StringBuffer buffer = new StringBuffer(16100);
    private boolean enableBuffering = false;

    @Override
    public Object deserialize(String raw) throws SerializationException {

        buffer.append(raw);

        // split up in different parts - based on the []
        // this is necessary because multiple objects can be chunked in one single string
        int brackets = 0;
        int start = 0;
        List<String> messages = new ArrayList<String>();
        int bufSize = buffer.length();
        for (int i = 0; i < bufSize; i++) {

            // detect brackets
            if (buffer.charAt(i) == '[') {
                // account for arrays which use [ as the leading part of the type encoding
                final int nextIndex = i + 1;
                if (nextIndex < bufSize) {
                    final char nextChar = buffer.charAt(nextIndex);
                    if (nextChar != 'L' &&
                            nextChar != 'B' &&
                            nextChar != 'C' &&
                            nextChar != 'S' &&
                            nextChar != 'I' &&
                            nextChar != 'J' &&
                            nextChar != 'F' &&
                            nextChar != 'D' &&
                            nextChar != 'Z') {
                        ++brackets;
                    }
                } else {
                    ++brackets;
                }
            } else if (buffer.charAt(i) == ']') {
                --brackets;
            }

            // new message
            if (brackets == 0) {
                messages.add(buffer.substring(start, i + 1));
                start = i + 1;
            }
        }
        buffer.delete(0, start);

        // create the objects
        List<Object> objects = new ArrayList<Object>();
        for (String message : messages) {
            try {
                Serializer serializer = getRPCSerializer();
                ClientSerializationStreamReader reader = new ClientSerializationStreamReader(serializer);
                reader.prepareToRead(message);
                objects.add(reader.readObject());
            } catch (RuntimeException e) {
                throw new SerializationException(e);
            }
        }
        return objects;
    }

    @Override
    public String serialize(Object message) throws SerializationException {
        try {
            Serializer serializer = getRPCSerializer();
            ClientSerializationStreamWriter writer = new ClientSerializationStreamWriter(serializer, GWT.getModuleBaseURL(), GWT.getPermutationStrongName());
            writer.prepareToWrite();
            writer.writeObject(message);
            return writer.toString();
        } catch (RuntimeException e) {
            throw new SerializationException(e);
        }
    }

    protected abstract Serializer getRPCSerializer();


    public boolean isEnableBuffering() {
        return enableBuffering;
    }

    /**
     * Set to true to enable multiple objects chunked in one single string parsing.
     * @param enableBuffering true to enable multiple objects chunked in one single string parsing.
     */
    public void setEnableBuffering(boolean enableBuffering) {
        this.enableBuffering = enableBuffering;
    }

}
