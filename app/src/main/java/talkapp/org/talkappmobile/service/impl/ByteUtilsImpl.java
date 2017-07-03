package talkapp.org.talkappmobile.service.impl;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

import talkapp.org.talkappmobile.service.ByteUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class ByteUtilsImpl implements ByteUtils {
    @Override
    public byte[] toPrimitives(Collection<Byte> collection) {
        ByteBuffer byteBuf = ByteBuffer.allocate(collection.size());
        for (Iterator<Byte> i = collection.iterator(); i.hasNext(); ) {
            byteBuf.put(i.next());
        }
        return byteBuf.array();
    }
}