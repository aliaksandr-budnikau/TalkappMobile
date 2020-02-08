package talkapp.org.talkappmobile.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewWordSetDraftQRObject implements Serializable {
    private final List<WordAndTranslationQRObject> wordTranslations;

    public NewWordSetDraftQRObject() {
        this.wordTranslations = new ArrayList<>();
    }

    public NewWordSetDraftQRObject(List<WordAndTranslationQRObject> wordTranslations) {
        this.wordTranslations = wordTranslations;
    }

    @NonNull
    public List<WordAndTranslationQRObject> getWordTranslations() {
        return wordTranslations;
    }
}