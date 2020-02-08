package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NewWordWithTranslation {
    private final String word;
    private final String translation;

    public NewWordWithTranslation(@NonNull String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NewWordWithTranslation that = (NewWordWithTranslation) o;

        return new EqualsBuilder()
                .append(word, that.word)
                .append(translation, that.translation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(word)
                .append(translation)
                .toHashCode();
    }
}