package talkapp.org.talkappmobile.component;

import talkapp.org.talkappmobile.activity.PracticeWordSetObserver;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.component.impl.GameProcesses;

/**
 * @author Budnikau Aliaksandr
 */
public interface GameProcessesFactory {

    GameProcesses createGameProcesses(WordSet wordSet, PracticeWordSetObserver observer);
}