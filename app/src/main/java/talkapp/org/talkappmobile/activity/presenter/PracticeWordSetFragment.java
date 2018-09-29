package talkapp.org.talkappmobile.activity.presenter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import talkapp.org.talkappmobile.R;
import talkapp.org.talkappmobile.activity.MainActivity;
import talkapp.org.talkappmobile.config.DIContext;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.WordSet;

import static android.os.AsyncTask.Status.RUNNING;

public class PracticeWordSetFragment extends Fragment implements PracticeWordSetView {
    public static final String WORD_SET_MAPPING = "wordSet";
    @Inject
    Executor executor;
    @Inject
    Handler uiEventHandler;

    private TextView originalText;
    private TextView rightAnswer;
    private TextView answerText;
    private ProgressBar recProgress;
    private ProgressBar wordSetProgress;
    private Button nextButton;
    private Button checkButton;
    private Button speakButton;
    private Button playButton;
    private LinearLayout spellingGrammarErrorsListView;
    private AsyncTask<Void, Void, Void> asyncTask;
    private PracticeWordSetPresenter presenter;

    private View.OnClickListener nextButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    presenter.onNextButtonClick();
                    return null;
                }
            }.executeOnExecutor(executor);
        }
    };

    private View.OnClickListener checkAnswerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    presenter.onCheckAnswerButtonClick(answerText.getText().toString());
                    return null;
                }
            }.executeOnExecutor(executor);
        }
    };

    private View.OnTouchListener rightAnswerOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    presenter.rightAnswerTouched();
                    return true; // if you want to handle the touch event
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    presenter.rightAnswerUntouched();
                    return true; // if you want to handle the touch event
            }
            return false;
        }
    };

    private View.OnClickListener recogniseVoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (asyncTask == null || asyncTask.getStatus() != RUNNING) {
                asyncTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        presenter.onRecogniseVoiceButtonClick();
                        return null;
                    }
                }.executeOnExecutor(executor);
                return;
            }
            presenter.onStopRecognitionVoiceButtonClick();
        }
    };

    private View.OnClickListener playVoiceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    presenter.onPlayVoiceButtonClick();
                    return null;
                }
            }.executeOnExecutor(executor);
        }
    };

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PracticeWordSetFragment newInstance(WordSet wordSet) {
        PracticeWordSetFragment fragment = new PracticeWordSetFragment();
        Bundle args = new Bundle();
        args.putSerializable(WORD_SET_MAPPING, wordSet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DIContext.get().inject(this);
        View inflate = inflater.inflate(R.layout.word_set_practice_activity_fragment, container, false);

        originalText = inflate.findViewById(R.id.originalText);
        rightAnswer = inflate.findViewById(R.id.rightAnswer);
        answerText = inflate.findViewById(R.id.answerText);
        recProgress = inflate.findViewById(R.id.recProgress);
        wordSetProgress = inflate.findViewById(R.id.wordSetProgress);
        nextButton = inflate.findViewById(R.id.nextButton);
        checkButton = inflate.findViewById(R.id.checkButton);
        speakButton = inflate.findViewById(R.id.speakButton);
        playButton = inflate.findViewById(R.id.playButton);

        nextButton.setOnClickListener(nextButtonListener);
        checkButton.setOnClickListener(checkAnswerButtonListener);
        speakButton.setOnClickListener(recogniseVoiceButtonListener);
        playButton.setOnClickListener(playVoiceButtonListener);

        WordSet wordSet = (WordSet) getArguments().get(WORD_SET_MAPPING);

        presenter = new PracticeWordSetPresenter(wordSet, this);

        spellingGrammarErrorsListView = inflate.findViewById(R.id.spellingGrammarErrorsListView);
        rightAnswer.setOnTouchListener(rightAnswerOnTouchListener);

        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                presenter.onResume();
                presenter.onNextButtonClick();
                return null;
            }
        }.executeOnExecutor(executor);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showNextButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                nextButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideNextButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                nextButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showCheckButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                checkButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideCheckButton() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                checkButton.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setRightAnswer(final String text) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                rightAnswer.setText(text);
            }
        });
    }

    @Override
    public void setProgress(int progress) {
        wordSetProgress.setProgress(progress);
    }

    @Override
    public void setOriginalText(final String text) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                originalText.setText(text);
            }
        });
    }

    @Override
    public void showMessageAnswerEmpty() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Answer can't be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void showMessageSpellingOrGrammarError() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Spelling or grammar errors", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showMessageAccuracyTooLow() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Accuracy too low", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void showCongratulationMessage() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Congratulations! You won!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void closeActivity() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                getActivity().finish();
            }
        });
    }

    @Override
    public void openAnotherActivity() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void setEnableVoiceRecButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                speakButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setEnablePlayButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                playButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setEnableCheckButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                checkButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setEnableNextButton(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                nextButton.setEnabled(value);
            }
        });
    }

    @Override
    public void setRecProgress(final int value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                recProgress.setProgress(value);
            }
        });
    }

    @Override
    public void hideRecProgress() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                recProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void setAnswerText(final String text) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                answerText.setText(text);
            }
        });
    }

    @Override
    public void setEnableRightAnswer(final boolean value) {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                rightAnswer.setEnabled(value);
            }
        });
    }

    @Override
    public void showSpellingOrGrammarErrorPanel(final String errorMessage) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                View vi = inflater.inflate(R.layout.row_spelling_grammar_errors_list_item, null);
                vi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
                TextView textView = vi.findViewById(R.id.errorRow);
                textView.setText(errorMessage);
                spellingGrammarErrorsListView.addView(vi);
            }
        });
    }

    @Override
    public void hideSpellingOrGrammarErrorPanel() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                spellingGrammarErrorsListView.removeAllViews();
            }
        });
    }

    @Override
    public void showRecProgress() {
        uiEventHandler.post(new Runnable() {
            @Override
            public void run() {
                recProgress.setVisibility(View.VISIBLE);
            }
        });
    }
}
