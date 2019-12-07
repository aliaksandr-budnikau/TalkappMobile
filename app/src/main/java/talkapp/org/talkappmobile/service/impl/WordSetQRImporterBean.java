package talkapp.org.talkappmobile.service.impl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonMappingException;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import java.io.IOException;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.AddingNewWordSetFragment_;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.WordSetQRImporter;
import talkapp.org.talkappmobile.service.WordSetService;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@EBean(scope = EBean.Scope.Singleton)
public class WordSetQRImporterBean implements WordSetQRImporter {
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @StringRes(R.string.adding_new_word_set_by_qrc_finished_successfully)
    String addingFinishedSuccessfullyMessage;
    @StringRes(R.string.adding_new_word_set_by_qrc_failed_json_format)
    String addingFailedMessageJsonFormat;
    @StringRes(R.string.adding_new_word_set_by_qrc_failed_mapping_error)
    String addingFailedMessageMappingError;

    @Override
    public void startScanActivity(Activity activity) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            activity.startActivityForResult(intent, 0);
        } catch (Exception e) {
            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            activity.startActivity(marketIntent);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String wordSetJson = data.getStringExtra("SCAN_RESULT");
                WordSetService wordSetService = serviceFactory.getWordSetExperienceRepository();
                try {
                    wordSetService.save(wordSetJson);
                } catch (JsonMappingException e) {
                    Toast.makeText(activity, addingFailedMessageMappingError, Toast.LENGTH_LONG).show();
                    return;
                } catch (IOException e) {
                    Toast.makeText(activity, addingFailedMessageJsonFormat, Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(activity, addingFinishedSuccessfullyMessage, Toast.LENGTH_LONG).show();
                activity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new AddingNewWordSetFragment_()).commit();
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }
}