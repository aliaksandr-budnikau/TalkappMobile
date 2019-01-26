package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

/**
 * @author Budnikau Aliaksandr
 */
public interface GitHubRestClient {

    String COMMON_PART = "/aliaksandr-budnikau/talkapp-data/master/";
    String SENTENCES_PATH = COMMON_PART + "sentences/l{wordsNumber}/ws{wordSetId}.json";
    String WORD_TRANSLATION_PATH = COMMON_PART + "words/{language}/ws{wordSetId}.json";
    String TOPICS_PATH = COMMON_PART + "topics/topics.json";
    String WORD_SETS_PATH = COMMON_PART + "wordsets/wordSets.json";

    @GET(SENTENCES_PATH)
    Call<Map<String, List<Sentence>>> findSentencesByWordSetId(@Path("wordSetId") int wordSetId, @Path("wordsNumber") int wordsNumber);

    @GET(WORD_TRANSLATION_PATH)
    Call<List<WordTranslation>> findWordTranslationsByWordSetIdAndByLanguage(@Path("wordSetId") int wordSetId, @Path("language") String language);

    @GET(TOPICS_PATH)
    Call<List<Topic>> findAllTopics();

    @GET(WORD_SETS_PATH)
    Call<Map<Integer, List<WordSet>>> findAllWordSets();
}