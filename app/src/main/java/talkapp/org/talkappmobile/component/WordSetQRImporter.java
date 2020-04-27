package talkapp.org.talkappmobile.component;

import android.app.Activity;
import android.content.Intent;

public interface WordSetQRImporter {
    void startScanActivity(Activity activity);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
}