package talkapp.org.talkappmobile.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.UnrecognizedVoice;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;

/**
 * @author Budnikau Aliaksandr
 */
public interface VoiceService {

    @POST("/voices/recognize")
    Call<VoiceRecognitionResult> recognize(@Body UnrecognizedVoice voice);
}
