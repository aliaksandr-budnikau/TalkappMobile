package talkapp.org.talkappmobile.activity;

import android.app.Activity;
import android.os.Bundle;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.service.WordSetService;

public class ExerciseActivity extends Activity {
    @Inject
    WordSetService wordSetService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        DIContext.get().inject(this);
    }
}
