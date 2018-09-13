package talkapp.org.talkappmobile.service.impl;

import talkapp.org.talkappmobile.activity.PracticeWordSetObserver;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.GameProcessesFactory;

/**
 * @author Budnikau Aliaksandr
 */
public class GameProcessesFactoryImpl implements GameProcessesFactory {

    @Override
    public GameProcesses createGameProcesses(WordSet wordSet, PracticeWordSetObserver observer) {
        return new GameProcesses(wordSet, observer);
    }
}