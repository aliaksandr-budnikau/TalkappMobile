package talkapp.org.talkappmobile.activity;

import android.os.Bundle;
import android.widget.TextView;

import talkapp.org.talkappmobile.R;

public class CrashActivity extends BaseActivity {

    public static final String STACK_TRACE = "stackTrace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        String stackTraceText = (String) getIntent().getSerializableExtra(STACK_TRACE);
        TextView stackTraceView = (TextView) findViewById(R.id.stackTrace);
        stackTraceView.setText(stackTraceText);
    }
}