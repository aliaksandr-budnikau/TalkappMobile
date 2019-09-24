package talkapp.org.talkappmobile.activity.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.controller.PhraseTranslationInputTextViewController;
import talkapp.org.talkappmobile.activity.custom.event.WordSetVocabularyItemViewLocalEventBus;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasUpdatedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

@EViewGroup(R.layout.word_set_vocabulary_item)
public class WordSetVocabularyItemView extends RelativeLayout implements WordSetVocabularyItemViewLocalEventBus {
    public static final int MIN_LINES = 2;
    @EventBusGreenRobot
    EventBus eventBus;

    @Bean(SpeakerBean.class)
    Speaker speaker;
    @Bean(ServiceFactoryBean.class)
    ServiceFactory serviceFactory;
    @Bean(BackendServerFactoryBean.class)
    BackendServerFactory backendServerFactory;

    @ViewById(R.id.word)
    TextView word;
    @ViewById(R.id.translation)
    TextView translation;

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

    private WordTranslation wordTranslation;

    public WordSetVocabularyItemView(Context context) {
        super(context);
    }

    public WordSetVocabularyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WordSetVocabularyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @AfterInject
    public void init() {
        controller = new PhraseTranslationInputTextViewController(eventBus, backendServerFactory.get(), serviceFactory, this);
    }

    public void pronounceTranslation() {
        if (wordTranslation == null) {
            return;
        }
        try {
            speaker.speak(wordTranslation.getWord());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void setModel(WordTranslation wordTranslation) {
        this.wordTranslation = wordTranslation;
    }

    public void refreshModel(boolean expanded) {
        word.setText(wordTranslation.getWord());
        translation.setText(wordTranslation.getTranslation());
        if (expanded) {
            if (translation.getLineCount() != 0) {
                translation.setLines(translation.getLineCount());
            }
            translation.setMinLines(MIN_LINES);
        } else {
            translation.setLines(MIN_LINES);
        }
    }

    public void openEditDialog() {
        Context context = this.getContext();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(popupTitle);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView messageForPhraseBox = new TextView(context);
        messageForPhraseBox.setText(popupPhraseLabel);
        layout.addView(messageForPhraseBox);

        phraseBox = new EditText(context);
        phraseBox.setHint(popupPhraseHint);
        phraseBox.setText(wordTranslation.getWord());
        layout.addView(phraseBox);

        final TextView messageForTranslationBox = new TextView(context);
        messageForTranslationBox.setText(popupTranslationLabel);
        layout.addView(messageForTranslationBox);

        translationBox = new EditText(context);
        translationBox.setHint(popupTranslationHint);
        translationBox.setText(wordTranslation.getTranslation());
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
                        onMessageEvent(new PhraseTranslationInputPopupOkClickedEM(phraseBox.getText().toString(),
                                translationBox.getText().toString()));
                    }
                });
            }
        });
        alertDialog.show();
    }

    @Override
    @Background
    public void onMessageEvent(PhraseTranslationInputPopupOkClickedEM event) {
        controller.handle(event);
    }

    @Override
    @UiThread
    public void onMessageEvent(NewWordIsEmptyEM event) {
        phraseBox.setError(warningEmptyField);
        translationBox.setError(null);
    }

    @Override
    @UiThread
    public void onMessageEvent(NewWordTranslationWasNotFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(warningTranslationNotFound);
    }

    @Override
    @UiThread
    public void onMessageEvent(NewWordSentencesWereNotFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(warningSentencesNotFound);
    }

    @Override
    @UiThread
    public void onMessageEvent(NewWordSentencesWereFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(null);
    }

    @Override
    @UiThread
    public void onMessageEvent(PhraseTranslationInputWasValidatedSuccessfullyEM event) {
        phraseBox.setError(null);
        translationBox.setError(null);
        wordTranslation.setWord(phraseBox.getText().toString());
        wordTranslation.setTranslation(translationBox.getText().toString());
        refreshModel(false);
        alertDialog.cancel();
        alertDialog.dismiss();
        alertDialog = null;
        eventBus.post(new PhraseTranslationInputWasUpdatedEM());
    }
}