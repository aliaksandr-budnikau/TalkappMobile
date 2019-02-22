package talkapp.org.talkappmobile.component.database;

public interface ServiceFactory {
    WordSetService getWordSetExperienceRepository();

    WordRepetitionProgressService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    LocalDataService getLocalDataService();
}