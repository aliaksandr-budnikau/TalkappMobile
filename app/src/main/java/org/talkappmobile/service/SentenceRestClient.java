package org.talkappmobile.service;

import org.talkappmobile.model.Sentence;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author Budnikau Aliaksandr
 */
public interface SentenceRestClient {

    String SENTENCE_PATH = "/sentence";
    String SENTENCE_SCORING_PATH = SENTENCE_PATH + "/score";

    @POST(SENTENCE_SCORING_PATH)
    Call<Boolean> saveSentenceScore(@Body Sentence sentence);
}
