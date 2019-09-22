package talkapp.org.talkappmobile.activity.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;

import talkapp.org.talkappmobile.R;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@EView
public class PhraseTranslationInputTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener {

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

    public PhraseTranslationInputTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    public void init() {
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(popupTitle);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView messageForPhraseBox = new TextView(context);
        messageForPhraseBox.setText(popupPhraseLabel);
        layout.addView(messageForPhraseBox);

        final EditText phraseBox = new EditText(context);
        phraseBox.setHint(popupPhraseHint);
        phraseBox.setText(phrase);
        layout.addView(phraseBox);

        final TextView messageForTranslationBox = new TextView(context);
        messageForTranslationBox.setText(popupTranslationLabel);
        layout.addView(messageForTranslationBox);

        final EditText translationBox = new EditText(context);
        translationBox.setHint(popupTranslationHint);
        translationBox.setText(translation);
        layout.addView(translationBox); // Another add method

        alertDialog.setView(layout); // Again this is a set method, not add

        alertDialog.setPositiveButton(popupButtonOk, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isEmpty(translationBox.getText())) {
                    PhraseTranslationInputTextView.this.setText(phraseBox.getText());
                } else {
                    String newInput = phraseBox.getText() + "|" + translationBox.getText();
                    PhraseTranslationInputTextView.this.setText(newInput);
                }
            }
        });

        alertDialog.setNegativeButton(popupButtonCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
}