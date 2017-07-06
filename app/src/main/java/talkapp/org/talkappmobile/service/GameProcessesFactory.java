package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.service.impl.GameProcessCallback;
import talkapp.org.talkappmobile.service.impl.GameProcesses;

/**
 * @author Budnikau Aliaksandr
 */
public interface GameProcessesFactory {

    GameProcesses createGameProcesses(WordSet currentWordSet, GameProcessCallback callback);
}