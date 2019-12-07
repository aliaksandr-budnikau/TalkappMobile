package talkapp.org.talkappmobile.model;

import java.io.Serializable;

/**
 * @author Budnikau Aliaksandr
 */
public class WordAndTranslationQRObject implements Serializable {
    private String word;
    private String translation;

    public WordAndTranslationQRObject() {
    }

    public WordAndTranslationQRObject(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }
}