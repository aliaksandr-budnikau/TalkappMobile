package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.service.mapper.WordSetMapper;

public interface ServiceFactory {
    WordSetService getWordSetExperienceRepository();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    LocalDataService getLocalDataService();

    WordSetMapper getWordSetMapper();
}