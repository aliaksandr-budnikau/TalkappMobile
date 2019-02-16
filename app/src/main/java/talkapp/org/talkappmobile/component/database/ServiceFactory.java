package talkapp.org.talkappmobile.component.database;

public interface ServiceFactory {
    WordSetService getWordSetExperienceRepository();

    PracticeWordSetExerciseService getPracticeWordSetExerciseRepository();

    UserExpService getUserExpService();

    LocalDataService getLocalDataService();
}