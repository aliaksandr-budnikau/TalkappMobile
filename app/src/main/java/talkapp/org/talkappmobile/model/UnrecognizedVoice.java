package talkapp.org.talkappmobile.model;

/**
 * @author Budnikau Aliaksandr
 */
public class UnrecognizedVoice {
    private byte[] voice;

    public byte[] getVoice() {
        return voice;
    }

    public void setVoice(byte[] voice) {
        this.voice = voice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnrecognizedVoice that = (UnrecognizedVoice) o;
        return voice.equals(that.voice);
    }

    @Override
    public int hashCode() {
        return voice.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UnrecognizedVoice{");
        sb.append("voice=").append("<bytes>");
        sb.append('}');
        return sb.toString();
    }
}