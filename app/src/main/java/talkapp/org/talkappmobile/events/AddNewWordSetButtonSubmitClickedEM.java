package talkapp.org.talkappmobile.events;

import java.util.List;

public class AddNewWordSetButtonSubmitClickedEM {
    private final List<String> words;

    public AddNewWordSetButtonSubmitClickedEM(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }
}