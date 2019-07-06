package org.talkappmobile.service;

import org.talkappmobile.service.mapper.WordSetMapper;

public interface ServiceFactory {
    WordSetService getWordSetExperienceRepository();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    LocalDataService getLocalDataService();

    WordSetMapper getWordSetMapper();
}