package talkapp.org.talkappmobile.component.backend;

import java.util.List;

import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.backend.impl.RegistrationException;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

public interface BackendServer {

    void registerAccount(Account account) throws RegistrationException;

    String loginUser(LoginCredentials credentials) throws LoginException;

    List<Sentence> findSentencesByWords(Word2Tokens words, int wordsNumber);

    List<GrammarError> checkText(String text);

    List<Topic> findAllTopics();

    List<WordSet> findAllWordSets();

    List<WordSet> findWordSetsByTopicId(int topicId);

    List<WordTranslation> findWordTranslationsByWordSetIdAndByLanguage(int wordSetId, String language);

    List<WordTranslation> findWordTranslationsByWordsAndByLanguage(List<String> words, String language);

    boolean saveSentenceScore(Sentence sentence);
}