package talkapp.org.talkappmobile.component.backend;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import talkapp.org.talkappmobile.model.UnrecognizedVoice;
import talkapp.org.talkappmobile.model.VoiceRecognitionResult;

/**
 * @author Budnikau Aliaksandr
 */
public interface VoiceService {

    @POST("/voices/recognize")
    Call<VoiceRecognitionResult> recognize(@Body UnrecognizedVoice voice, @HeaderMap Map<String, String> headers);
}
