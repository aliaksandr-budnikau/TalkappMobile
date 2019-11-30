package talkapp.org.talkappmobile.activity.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;

import talkapp.org.talkappmobile.R;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@EBean(scope = EBean.Scope.Singleton)
public class WordSetVocabularyItemAlertDialog {
    public static final int LEFT_PADDING_LABELS = 50;
    public static final int LEFT_PADDING_INPUTS = 10;
    public static final int BOTTOM_PADDING_INPUTS = 30;
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
    private OnDialogInteractionListener onDialogInteractionListener;
    private EditText phraseBox;
    private EditText translationBox;
    private AlertDialog alertDialog;


    public void setOnDialogInteractionListener(OnDialogInteractionListener onDialogInteractionListener) {
        this.onDialogInteractionListener = onDialogInteractionListener;
    }

    public void open(final String origPhrase, final String origTranslation, Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(popupTitle);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView messageForPhraseBox = new TextView(context);
        messageForPhraseBox.setText(popupPhraseLabel);
        messageForPhraseBox.setPadding(LEFT_PADDING_LABELS, 0, 0, 0);
        layout.addView(messageForPhraseBox);

        phraseBox = new EditText(context);
        phraseBox.setHint(popupPhraseHint);
        phraseBox.setText(isEmpty(origPhrase) ? "" : origPhrase);
        phraseBox.setPadding(LEFT_PADDING_INPUTS, 0, 0, BOTTOM_PADDING_INPUTS);
        layout.addView(phraseBox);

        final TextView messageForTranslationBox = new TextView(context);
        messageForTranslationBox.setText(popupTranslationLabel);
        messageForTranslationBox.setPadding(LEFT_PADDING_LABELS, 0, 0, 0);
        layout.addView(messageForTranslationBox);

        translationBox = new EditText(context);
        translationBox.setHint(popupTranslationHint);
        translationBox.setText(isEmpty(origTranslation) ? "" : origTranslation);
        translationBox.setPadding(LEFT_PADDING_INPUTS, 0, 0, BOTTOM_PADDING_INPUTS);
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
                        String newPhrase = phraseBox.getText().toString();
                        String newTranslation = translationBox.getText().toString();
                        onDialogInteractionListener.onOkButtonClicked(newPhrase, newTranslation, origPhrase, origTranslation);
                    }
                });
            }
        });
        alertDialog.show();
    }

    public void setPhraseBoxError(String error) {
        phraseBox.setError(error);
    }

    public void setTranslationBoxError(String error) {
        translationBox.setError(error);
    }

    public String getPhraseBoxText() {
        return String.valueOf(phraseBox.getText());
    }

    public String getTranslationBoxText() {
        return String.valueOf(translationBox.getText());
    }

    public void cancel() {
        alertDialog.cancel();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    public interface OnDialogInteractionListener {
        void onOkButtonClicked(String newPhrase, String newTranslation, String origPhrase, String origTranslation);
    }
}