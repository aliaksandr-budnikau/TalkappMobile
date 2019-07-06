package org.talkappmobile.activity;

import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import org.talkappmobile.R;

@EActivity(R.layout.activity_crash)
public class CrashActivity extends BaseActivity {

    public static final String STACK_TRACE = "stackTrace";

    @ViewById(R.id.stackTrace)
    TextView stackTraceView;

    @Extra(STACK_TRACE)
    String stackTraceText;

    @AfterViews
    public void init() {
        stackTraceView.setText(stackTraceText);
    }
}