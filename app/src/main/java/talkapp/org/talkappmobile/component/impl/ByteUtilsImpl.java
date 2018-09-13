package talkapp.org.talkappmobile.component.impl;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

import talkapp.org.talkappmobile.component.ByteUtils;

/**
 * @author Budnikau Aliaksandr
 */
public class ByteUtilsImpl implements ByteUtils {

    private static final int AMPLITUDE_THRESHOLD = 1500;

    @Override
    public byte[] toPrimitives(Collection<Byte> collection) {
        ByteBuffer byteBuf = ByteBuffer.allocate(collection.size());
        for (Iterator<Byte> i = collection.iterator(); i.hasNext(); ) {
            byteBuf.put(i.next());
        }
        return byteBuf.array();
    }

    @Override
    public boolean isHearingVoice(byte[] buffer, int size) {
        for (int i = 0; i < size - 1; i += 2) {
            int s = buffer[i + 1];
            if (s < 0) s *= -1;
            s <<= 8;
            s += Math.abs(buffer[i]);
            if (s > AMPLITUDE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }
}