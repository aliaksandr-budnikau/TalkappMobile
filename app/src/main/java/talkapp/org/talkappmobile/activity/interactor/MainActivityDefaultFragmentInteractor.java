package talkapp.org.talkappmobile.activity.interactor;

import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.component.database.PracticeWordSetExerciseService;
import talkapp.org.talkappmobile.model.WordSet;

public class MainActivityDefaultFragmentInteractor {

    private final PracticeWordSetExerciseService exerciseService;

    public MainActivityDefaultFragmentInteractor(PracticeWordSetExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public void initWordsForRepetition(OnMainActivityDefaultFragmentListener listener) {
        List<WordSet> sets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        listener.onWordsForRepetitionCounted(sets.size());
    }
}