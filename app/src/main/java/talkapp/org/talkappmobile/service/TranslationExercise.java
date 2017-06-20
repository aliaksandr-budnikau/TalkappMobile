package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.AnswerCheckingResult;

/**
 * @author Budnikau Aliaksandr
 */
public interface TranslationExercise extends Runnable {
    void analyzeCheckingResult(AnswerCheckingResult result);

    void setDataSource(DataSource dataSource);

    void setEventHandler(EventHandler eventHandler);
}
