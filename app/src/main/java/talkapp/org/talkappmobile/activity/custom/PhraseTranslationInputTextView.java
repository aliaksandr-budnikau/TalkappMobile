package talkapp.org.talkappmobile.activity.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.controller.PhraseTranslationInputTextViewController;
import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@EView
public class PhraseTranslationInputTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

    @EventBusGreenRobot
    EventBus eventBus;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;

    @StringRes(R.string.phrase_translation_input_text_view_popup_title)
    String popupTitle;
    @StringRes(R.string.phrase_translation_input_text_view_popup_phrase_label)
    String popupPhraseLabel;
    @StringRes(R.string.phrase_translation_input_text_view_popup_translation_label)
    String popupTranslationLabel;
    @StringRes(R.string.phrase_translation_input_text_view_popup_phrase_hint)
    String popupPhraseHint;
    @StringRes(R.string.phrase_translation_input_text_view_popup_translation_hint)
    String popupTranslationHint;
    @StringRes(R.string.phrase_translation_input_text_view_popup_button_ok)
    String popupButtonOk;
    @StringRes(R.string.phrase_translation_input_text_view_popup_button_cancel)
    String popupButtonCancel;
    @StringRes(R.string.adding_new_word_set_fragment_warning_empty_field)
    String warningEmptyField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_translation_not_found)
    String warningTranslationNotFound;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;

    private PhraseTranslationInputTextViewController controller;
    private EditText phraseBox;
    private EditText translationBox;
    private AlertDialog alertDialog;

    public PhraseTranslationInputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    public void init() {
        setOnClickListener(this);
        controller = new PhraseTranslationInputTextViewController(eventBus, backendServerFactory.get(), serviceFactory);
    }

    @Override
    public void onClick(final View v) {
        String originText = getOriginText();
        String[] phraseAndTranslation = originText.split("\\|");
        String phrase = null;
        String translation = null;
        if (phraseAndTranslation.length == 2) {
            phrase = phraseAndTranslation[0].trim();
            translation = phraseAndTranslation[1].trim();
        } else {
            phrase = phraseAndTranslation[0].trim();
        }

        Context context = v.getContext();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(popupTitle);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView messageForPhraseBox = new TextView(context);
        messageForPhraseBox.setText(popupPhraseLabel);
        layout.addView(messageForPhraseBox);

        phraseBox = new EditText(context);
        phraseBox.setHint(popupPhraseHint);
        phraseBox.setText(phrase);
        layout.addView(phraseBox);

        final TextView messageForTranslationBox = new TextView(context);
        messageForTranslationBox.setText(popupTranslationLabel);
        layout.addView(messageForTranslationBox);

        translationBox = new EditText(context);
        translationBox.setHint(popupTranslationHint);
        translationBox.setText(translation);
        layout.addView(translationBox); // Another add method

        alertDialogBuilder.setView(layout); // Again this is a set method, not add
        alertDialogBuilder.setPositiveButton(popupButtonOk, null);
        alertDialogBuilder.setNegativeButton(popupButtonCancel, null);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        eventBus.post(new PhraseTranslationInputPopupOkClickedEM(phraseBox.getText().toString(),
                                translationBox.getText().toString()));
                    }
                });
            }
        });
        alertDialog.show();
    }

    @NonNull
    private String getOriginText() {
        CharSequence originText = PhraseTranslationInputTextView.this.getText();
        if (originText == null) {
            return "";
        }
        return originText.toString();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(PhraseTranslationInputPopupOkClickedEM event) {
        controller.handle(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordIsEmptyEM event) {
        phraseBox.setError(warningEmptyField);
        translationBox.setError(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordTranslationWasNotFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(warningTranslationNotFound);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSentencesWereNotFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(warningSentencesNotFound);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordSentencesWereFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhraseTranslationInputWasValidatedSuccessfullyEM event) {
        phraseBox.setError(null);
        translationBox.setError(null);
        if (isEmpty(translationBox.getText())) {
            this.setText(phraseBox.getText());
        } else {
            String newInput = phraseBox.getText() + "|" + translationBox.getText();
            this.setText(newInput);
        }
        alertDialog.cancel();
    }
}