package talkapp.org.talkappmobile.language.stemmer;

import java.util.List;

public interface StemmerAdapter {
    List<String> stem(String text);
}