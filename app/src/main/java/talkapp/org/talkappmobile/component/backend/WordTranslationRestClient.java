package talkapp.org.talkappmobile.component.backend;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Query;
import talkapp.org.talkappmobile.model.WordTranslation;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordTranslationRestClient {

    String WORD_TRANSLATION_PATH = "/wordtranslation";

    @GET(WORD_TRANSLATION_PATH)
    Call<List<WordTranslation>> findByWordsAndByLanguage(@Query("word") List<String> words, @Query("language") String language, @HeaderMap Map<String, String> headers);
}