package talkapp.org.talkappmobile.activity.custom;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.model.WordTranslation;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

@EView
public class WordSetVocabularyView extends RecyclerView {
    @StringRes(R.string.phrase_translation_hidden_button_say_label)
    String hiddenButtonSayLabel;
    @StringRes(R.string.phrase_translation_hidden_button_reset_label)
    String hiddenButtonResetLabel;
    @StringRes(R.string.phrase_translation_hidden_button_edit_label)
    String hiddenButtonEditLabel;
    @StringRes(R.string.adding_new_word_set_fragment_warning_sentences_not_found)
    String warningSentencesNotFound;
    @StringRes(R.string.phrase_translation_warning_read_only_mode)
    String warningReadOnlyMode;

    private boolean readOnly;
    private OnItemViewInteractionListener onItemViewInteractionListener = new OnItemViewInteractionListener() {
        @Override
        public void onSayItemButtonClicked(WordTranslation item, int position) {
            // do nothing
        }

        @Override
        public void onEditItemButtonClicked(WordTranslation item, int position) {
            // do nothing
        }
    };
    private int editedItemPosition = -1;

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

    public void setOnItemViewInteractionListener(OnItemViewInteractionListener onItemViewInteractionListener) {
        this.onItemViewInteractionListener = onItemViewInteractionListener;
    }

    @AfterInject
    public void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        addItemDecoration(itemDecor);
        SwipeController swipeController = new SwipeController(new SwipeControllerActions());
        new ItemTouchHelper(swipeController).attachToRecyclerView(this);
        setupRecyclerView(swipeController);
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

    public void submitItemChange(String phrase, String translation) {
        if (editedItemPosition != -1) {
            getVocabulary().get(editedItemPosition).setWord(phrase);
            getVocabulary().get(editedItemPosition).setTranslation(translation);
            getAdapter().notifyDataSetChanged();
        }
        editedItemPosition = -1;
    }

    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    public interface OnItemViewInteractionListener {
        void onSayItemButtonClicked(WordTranslation item, int position);

        void onEditItemButtonClicked(WordTranslation item, int position);
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
            onItemViewInteractionListener.onSayItemButtonClicked(getVocabulary().get(position), position);
        }

        public void onEditButtonClicked(int position) {
            if (readOnly) {
                Toast.makeText(WordSetVocabularyView.this.getContext(), warningReadOnlyMode, Toast.LENGTH_LONG).show();
            } else {
                editedItemPosition = position;
                onItemViewInteractionListener.onEditItemButtonClicked(getVocabulary().get(position), editedItemPosition);
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