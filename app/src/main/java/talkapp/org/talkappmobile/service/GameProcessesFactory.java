package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.activity.PracticeWordSetObserver;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.impl.GameProcesses;

/**
 * @author Budnikau Aliaksandr
 */
public interface GameProcessesFactory {

    GameProcesses createGameProcesses(WordSet wordSet, PracticeWordSetObserver observer);
}