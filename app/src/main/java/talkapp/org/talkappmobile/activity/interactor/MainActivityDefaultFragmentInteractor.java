package talkapp.org.talkappmobile.activity.interactor;

import java.util.Iterator;
import java.util.List;

import talkapp.org.talkappmobile.activity.listener.OnMainActivityDefaultFragmentListener;
import talkapp.org.talkappmobile.component.database.WordRepetitionProgressService;
import talkapp.org.talkappmobile.model.WordSet;

public class MainActivityDefaultFragmentInteractor {

    private final WordRepetitionProgressService exerciseService;

    public MainActivityDefaultFragmentInteractor(WordRepetitionProgressService exerciseService) {
        this.exerciseService = exerciseService;
    }

    public void initWordsForRepetition(OnMainActivityDefaultFragmentListener listener) {
        List<WordSet> sets = exerciseService.findFinishedWordSetsSortByUpdatedDate(24 * 2);
        filterOutSmallWordSets(sets);
        listener.onWordsForRepetitionCounted(sets.size());
    }

    private void filterOutSmallWordSets(List<WordSet> sets) {
        int maxWordSetSize = exerciseService.getMaxWordSetSize();
        Iterator<WordSet> iterator = sets.iterator();
        while (iterator.hasNext()) {
            WordSet set = iterator.next();
            if (set.getWords().size() < maxWordSetSize) {
                iterator.remove();
            }
        }
    }
}