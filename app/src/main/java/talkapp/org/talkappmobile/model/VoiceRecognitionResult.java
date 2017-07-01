package talkapp.org.talkappmobile.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class VoiceRecognitionResult {
    private List<String> variant = new LinkedList<>();

    public List<String> getVariant() {
        return variant;
    }

    public void setVariant(List<String> variant) {
        this.variant = variant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoiceRecognitionResult that = (VoiceRecognitionResult) o;
        return Objects.equals(variant, that.variant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variant);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VoiceRecognitionResult{");
        sb.append("variant=").append(variant);
        sb.append('}');
        return sb.toString();
    }
}