package talkapp.org.talkappmobile.activity.custom;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tmtron.greenannotations.EventBusGreenRobot;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import talkapp.org.talkappmobile.model.WordTranslation;
import talkapp.org.talkappmobile.service.BackendServerFactory;
import talkapp.org.talkappmobile.service.ServiceFactory;
import talkapp.org.talkappmobile.service.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.service.impl.ServiceFactoryBean;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;
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
    @StringRes(R.string.phrase_translation_hidden_button_say_label)
    String hiddenButtonSayLabel;
    @StringRes(R.string.phrase_translation_hidden_button_reset_label)
    String hiddenButtonResetLabel;
    @StringRes(R.string.phrase_translation_hidden_button_edit_label)
    String hiddenButtonEditLabel;
    @StringRes(R.string.adding_new_word_set_fragment_warning_empty_field)
    String warningEmptyField;
    @StringRes(R.string.adding_new_word_set_fragment_warning_translation_not_found)
    String warningTranslationNotFound;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;
    @StringRes(R.string.phrase_translation_warning_read_only_mode)
    String warningReadOnlyMode;

    private AlertDialog alertDialog;

    private WordSetVocabularyViewController controller;
    private EditText phraseBox;
    private EditText translationBox;
    private boolean readOnly;

    public WordSetVocabularyView(Context context) {
        super(context);
    }

    public WordSetVocabularyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.WordSetVocabularyView);
        readOnly = arr.getBoolean(R.styleable.WordSetVocabularyView_readOnly, true);
        arr.recycle();
    }

    public WordSetVocabularyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterInject
    public void init() {
        controller = new WordSetVocabularyViewController(eventBus, backendServerFactory.get(), serviceFactory);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        addItemDecoration(itemDecor);
        SwipeController swipeController = new SwipeController(new SwipeControllerActions());
        new ItemTouchHelper(swipeController).attachToRecyclerView(this);
        setupRecyclerView(swipeController);
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
        Adapter adapter = this.getAdapter();
        if (adapter == null) {
            return null;
        }
        return ((VocabularyAdapter) adapter).getTranslations();
    }

    public void resetVocabulary() {
        List<WordTranslation> translations = ((VocabularyAdapter) this.getAdapter()).getTranslations();
        for (WordTranslation translation : translations) {
            translation.setWord("");
            translation.setTranslation("");
        }
    }

    public void resetVocabularyItem(int position) {
        WordTranslation translation = ((VocabularyAdapter) this.getAdapter()).getTranslations().get(position);
        translation.setWord("");
        translation.setTranslation("");
    }

    private void setupRecyclerView(final SwipeController swipeController) {
        this.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    public static class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.ViewHolder> {
        private final WordTranslationExpandable[] translations;

        public VocabularyAdapter(WordTranslation[] translations) {
            this.translations = new WordTranslationExpandable[translations.length];
            for (int i = 0; i < translations.length; i++) {
                this.translations[i] = new WordTranslationExpandable(translations[i]);
            }
        }

        @NonNull
        @Override
        public VocabularyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            WordSetVocabularyItemView v = WordSetVocabularyItemView_.build(parent.getContext());
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    private class SwipeControllerActions {
        public void onSayButtonClicked(int position) {
            pronounceText(getVocabulary().get(position).getWord());
        }

        public void onEditButtonClicked(int position) {
            if (readOnly) {
                Toast.makeText(WordSetVocabularyView.this.getContext(), warningReadOnlyMode, Toast.LENGTH_LONG).show();
            } else {
                openAlertDialog(getVocabulary().get(position), position);
                getAdapter().notifyItemChanged(position);
            }
        }

        public void onResetButtonClicked(int position) {
            if (readOnly) {
                Toast.makeText(WordSetVocabularyView.this.getContext(), warningReadOnlyMode, Toast.LENGTH_LONG).show();
            } else {
                resetVocabularyItem(position);
                getAdapter().notifyItemChanged(position);
            }
        }
    }

    private class SwipeController extends ItemTouchHelper.Callback {

        private static final float buttonWidth = 120;
        private static final String SAY_BUTTON = "SAY_BUTTON";
        private static final String EDIT_BUTTON = "EDIT_BUTTON";
        private static final String RESET_BUTTON = "RESET_BUTTON";
        private boolean swipeBack = false;
        private ButtonsState buttonShowedState = ButtonsState.GONE;
        private HashMap<String, RectF> buttonInstances = null;
        private RecyclerView.ViewHolder currentItemViewHolder = null;
        private SwipeControllerActions buttonsActions = null;

        public SwipeController(SwipeControllerActions buttonsActions) {
            this.buttonsActions = buttonsActions;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, LEFT | RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (swipeBack) {
                swipeBack = buttonShowedState != ButtonsState.GONE;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ACTION_STATE_SWIPE) {
                if (buttonShowedState != ButtonsState.GONE) {
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE)
                        dX = Math.max(dX, buttonWidth);
                    if (buttonShowedState == ButtonsState.RIGHT_VISIBLE)
                        dX = Math.min(dX, -buttonWidth * 2);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                } else {
                    setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            if (buttonShowedState == ButtonsState.GONE) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            currentItemViewHolder = viewHolder;
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                    if (swipeBack) {
                        if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                        else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE;

                        if (buttonShowedState != ButtonsState.GONE) {
                            setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            setItemsClickable(recyclerView, false);
                        }
                    }
                    return false;
                }
            });
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    }
                    return false;
                }
            });
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        SwipeController.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                        recyclerView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                        setItemsClickable(recyclerView, true);
                        swipeBack = false;

                        if (buttonsActions != null && buttonInstances != null) {
                            for (Map.Entry<String, RectF> entry : buttonInstances.entrySet()) {
                                if (entry.getValue().contains(event.getX(), event.getY())) {
                                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                                        if (SAY_BUTTON.equals(entry.getKey())) {
                                            buttonsActions.onSayButtonClicked(viewHolder.getAdapterPosition());
                                        }
                                    } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                                        if (EDIT_BUTTON.equals(entry.getKey())) {
                                            buttonsActions.onEditButtonClicked(viewHolder.getAdapterPosition());
                                        } else if (RESET_BUTTON.equals(entry.getKey())) {
                                            buttonsActions.onResetButtonClicked(viewHolder.getAdapterPosition());
                                        }
                                    }
                                }
                            }
                        }
                        buttonShowedState = ButtonsState.GONE;
                        currentItemViewHolder = null;
                    }
                    return false;
                }
            });
        }

        private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
            for (int i = 0; i < recyclerView.getChildCount(); ++i) {
                recyclerView.getChildAt(i).setClickable(isClickable);
            }
        }

        private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
            int separator = 5;
            float buttonWidthWithoutPadding = buttonWidth - separator;
            float corners = 8;

            View itemView = viewHolder.itemView;
            Paint p = new Paint();

            RectF sayButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
            p.setColor(getResources().getColor(R.color.colorPrimaryLighter));
            c.drawRoundRect(sayButton, corners, corners, p);
            drawText(hiddenButtonSayLabel, c, sayButton, p);

            RectF editButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            p.setColor(getResources().getColor(R.color.colorPrimaryLighter));
            c.drawRoundRect(editButton, corners, corners, p);
            drawText(hiddenButtonEditLabel, c, editButton, p);

            int buttonSeparator = separator / 2;
            RectF resetButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding * 2 - buttonSeparator, itemView.getTop(), itemView.getRight() - buttonWidthWithoutPadding - buttonSeparator, itemView.getBottom());
            p.setColor(getResources().getColor(R.color.colorPrimaryLighter));
            c.drawRoundRect(resetButton, corners, corners, p);
            drawText(hiddenButtonResetLabel, c, resetButton, p);

            buttonInstances = null;
            if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                buttonInstances = new HashMap<>();
                buttonInstances.put(SAY_BUTTON, sayButton);
            } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                buttonInstances = new HashMap<>();
                buttonInstances.put(EDIT_BUTTON, editButton);
                buttonInstances.put(RESET_BUTTON, resetButton);
            }
        }

        public void onDraw(Canvas c) {
            if (currentItemViewHolder != null) {
                drawButtons(c, currentItemViewHolder);
            }
        }

        @Override
        public void onSwiped(@NonNull ViewHolder viewHolder, int swipeDir) {
        }

        private void drawText(String text, Canvas c, RectF button, Paint p) {
            float textSize = 17;
            p.setColor(getResources().getColor(R.color.color_word_set_vocabulary_item_word));
            p.setAntiAlias(true);
            p.setTextSize(textSize);

            float textWidth = p.measureText(text);
            c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
        }
    }
}