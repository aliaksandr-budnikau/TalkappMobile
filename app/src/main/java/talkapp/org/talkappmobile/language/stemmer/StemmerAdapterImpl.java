package talkapp.org.talkappmobile.language.stemmer;

import android.content.Context;
import android.content.res.AssetManager;

import org.apache.lucene.util.CharsRef;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import hunspell_stemmer.Dictionary;
import hunspell_stemmer.Stemmer;

import static java.lang.String.format;

public class StemmerAdapterImpl implements StemmerAdapter {
    static final String LANGUAGE_DIC_EN_US_AFF = "language_dic/en_US.aff";
    static final String LANGUAGE_DIC_EN_US_DIC = "language_dic/en_US.dic";
    private final Stemmer stemmer;

    public StemmerAdapterImpl(Context context) {
        AssetManager am = context.getAssets();
        InputStream affixInput = null;
        InputStream dictionaryInput = null;
        try {
            affixInput = am.open(LANGUAGE_DIC_EN_US_AFF);
            dictionaryInput = am.open(LANGUAGE_DIC_EN_US_DIC);
            Dictionary dictionary = new Dictionary(affixInput, dictionaryInput);
            stemmer = new Stemmer(dictionary);
        } catch (Exception e) {
            throw new RuntimeException(format("affPath '%s' dicPath '%s'", LANGUAGE_DIC_EN_US_AFF, LANGUAGE_DIC_EN_US_DIC), e);
        } finally {
            try {
                close(dictionaryInput);
            } finally {
                close(affixInput);
            }
        }
    }

    @Override
    public List<String> stem(String text) {
        List<CharsRef> stem = stemmer.stem(text.toCharArray(), text.length());
        LinkedList<String> result = new LinkedList<>();
        for (CharsRef charsRef : stem) {
            result.add(charsRef.toString());
        }
        return result;
    }

    private void close(InputStream stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}