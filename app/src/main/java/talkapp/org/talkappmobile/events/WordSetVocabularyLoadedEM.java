package talkapp.org.talkappmobile.events;

import org.talkappmobile.model.WordTranslation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class WordSetVocabularyLoadedEM {
    @NonNull
    private WordTranslation[] translations;
}