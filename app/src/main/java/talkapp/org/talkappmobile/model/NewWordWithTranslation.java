package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.util.Objects;

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
        return word.equals(that.word) && Objects.equals(translation, that.translation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word, translation);
    }
}