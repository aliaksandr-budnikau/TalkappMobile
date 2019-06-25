package talkapp.org.talkappmobile.events;

import org.talkappmobile.model.Sentence;
import org.talkappmobile.model.Word2Tokens;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class SentencesWereFoundForChangeEM {
    @NonNull
    private List<Sentence> sentences;
    @NonNull
    private List<Sentence> alreadyPickedSentences;
    @NonNull
    private Word2Tokens word;
}