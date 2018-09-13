package talkapp.org.talkappmobile.component;

import java.util.Collection;

/**
 * @author Budnikau Aliaksandr
 */
public interface ByteUtils {

    byte[] toPrimitives(Collection<Byte> collection);

    boolean isHearingVoice(byte[] buffer, int size);
}