package talkapp.org.talkappmobile.activity.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.LinkedList;
import java.util.List;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.custom.controller.WordSetVocabularyViewController;
import talkapp.org.talkappmobile.component.Speaker;
import talkapp.org.talkappmobile.component.impl.SpeakerBean;
import talkapp.org.talkappmobile.events.NewWordIsEmptyEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereFoundEM;
import talkapp.org.talkappmobile.events.NewWordSentencesWereNotFoundEM;
import talkapp.org.talkappmobile.events.NewWordTranslationWasNotFoundEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputPopupOkClickedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasUpdatedEM;
import talkapp.org.talkappmobile.events.PhraseTranslationInputWasValidatedSuccessfullyEM;
import talkapp.org.talkappmobile.events.WordSetVocabularyLoadedEM;
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@EView
public class WordSetVocabularyView extends RecyclerView {
    @EventBusGreenRobot
    EventBus eventBus;

    @Bean(SpeakerBean.class)
    Speaker speaker;
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

    private AlertDialog alertDialog;

    private WordSetVocabularyViewController controller;
    private EditText phraseBox;
    private EditText translationBox;

    public WordSetVocabularyView(Context context) {
        super(context);
    }

    public WordSetVocabularyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterInject
    public void init() {
        controller = new WordSetVocabularyViewController(eventBus, backendServerFactory.get(), serviceFactory);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        addItemDecoration(itemDecor);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, @NonNull ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull ViewHolder viewHolder, int swipeDir) {
                int adapterPosition = viewHolder.getAdapterPosition();
                WordTranslation translation = getVocabulary().get(adapterPosition);
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    pronounceText(translation.getWord());
                } else if (swipeDir == ItemTouchHelper.LEFT) {
                    openAlertDialog(translation, adapterPosition);
                }
                getAdapter().notifyItemChanged(adapterPosition);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(WordSetVocabularyLoadedEM event) {
        WordTranslation[] translations = event.getTranslations().toArray(new WordTranslation[0]);
        this.setAdapter(new VocabularyAdapter(translations));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PhraseTranslationInputWasUpdatedEM event) {
        this.getAdapter().notifyDataSetChanged();
    }

    public void pronounceText(String text) {
        try {
            speaker.speak(text);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void openAlertDialog(final WordTranslation wordTranslation, final int adapterPosition) {
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
        phraseBox.setText(isEmpty(wordTranslation.getWord()) ? "" : wordTranslation.getWord());
        layout.addView(phraseBox);

        final TextView messageForTranslationBox = new TextView(context);
        messageForTranslationBox.setText(popupTranslationLabel);
        layout.addView(messageForTranslationBox);

        translationBox = new EditText(context);
        translationBox.setHint(popupTranslationHint);
        translationBox.setText(isEmpty(wordTranslation.getTranslation()) ? "" : wordTranslation.getTranslation());
        layout.addView(translationBox); // Another add method

        alertDialogBuilder.setView(layout); // Again this is a set method, not add
        alertDialogBuilder.setPositiveButton(popupButtonOk, null);
        alertDialogBuilder.setNegativeButton(popupButtonCancel, null);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        eventBus.post(new PhraseTranslationInputPopupOkClickedEM(adapterPosition, phraseBox.getText().toString(),
                                translationBox.getText().toString()));
                    }
                });
            }
        });
        alertDialog.show();
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
    public void onMessageEvent(NewWordSentencesWereNotFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(warningSentencesNotFound);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewWordTranslationWasNotFoundEM event) {
        phraseBox.setError(null);
        translationBox.setError(warningTranslationNotFound);
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
        WordTranslation translation = getVocabulary().get(event.getAdapterPosition());
        translation.setWord(phraseBox.getText().toString());
        translation.setTranslation(translationBox.getText().toString());
        alertDialog.cancel();
        alertDialog.dismiss();
        alertDialog = null;
        eventBus.post(new PhraseTranslationInputWasUpdatedEM());
    }

    public List<WordTranslation> getVocabulary() {
        return ((VocabularyAdapter) this.getAdapter()).getTranslations();
    }

    public void resetVocabulary() {
        List<WordTranslation> translations = ((VocabularyAdapter) this.getAdapter()).getTranslations();
        for (WordTranslation translation : translations) {
            translation.setWord("");
            translation.setTranslation("");
        }
    }

    private static class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
        private final WordTranslationExpandable[] translations;

        private VocabularyAdapter(WordTranslation[] translations) {
            this.translations = new WordTranslationExpandable[translations.length];
            for (int i = 0; i < translations.length; i++) {
                this.translations[i] = new WordTranslationExpandable(translations[i]);
            }
        }

        @Override
        public VocabularyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = WordSetVocabularyItemView_.build(parent.getContext());
            return new ViewHolder((WordSetVocabularyItemView) v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(this.translations[position], position);
        }

        @Override
        public int getItemCount() {
            return translations.length;
        }

        public List<WordTranslation> getTranslations() {
            LinkedList<WordTranslation> wordTranslations = new LinkedList<>();
            for (WordTranslationExpandable translation : translations) {
                wordTranslations.add(translation.getTranslation());
            }
            return wordTranslations;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final WordSetVocabularyItemView view;

            private ViewHolder(WordSetVocabularyItemView view) {
                super(view);
                this.view = view;
            }

            void bind(final WordTranslationExpandable translation, final int position) {
                view.refreshModel(translation.getTranslation(), translation.isExpanded());
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean expanded = translation.isExpanded();
                        translation.setExpanded(!expanded);
                        notifyItemChanged(position);
                    }
                });
            }

            public WordSetVocabularyItemView getView() {
                return view;
            }
        }
    }

    private static class WordTranslationExpandable {
        private WordTranslation translation;
        private boolean expanded;

        WordTranslationExpandable(WordTranslation translation) {
            this.translation = translation;
        }

        public WordTranslation getTranslation() {
            return translation;
        }

        public void setTranslation(WordTranslation translation) {
            this.translation = translation;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }
    }
}