package talkapp.org.talkappmobile.component.database;

public interface ServiceFactory {
    WordSetExperienceService getWordSetExperienceRepository();

    PracticeWordSetExerciseService getPracticeWordSetExerciseRepository();
}