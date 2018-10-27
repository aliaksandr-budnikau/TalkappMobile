package talkapp.org.talkappmobile.module;

import com.j256.ormlite.dao.Dao;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import talkapp.org.talkappmobile.component.database.DatabaseHelper;
import talkapp.org.talkappmobile.component.database.dao.PracticeWordSetExerciseDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetExperienceDao;
import talkapp.org.talkappmobile.component.database.mappings.PracticeWordSetExerciseMapping;
import talkapp.org.talkappmobile.component.database.mappings.WordSetExperienceMapping;
import talkapp.org.talkappmobile.model.WordSetExperienceStatus;

public class TestDatabaseModule extends DatabaseModule {

    @Override
    public PracticeWordSetExerciseDao providePracticeWordSetExerciseDao(DatabaseHelper databaseHelper) {
        return new PracticeWordSetExerciseDao() {
            private Set<PracticeWordSetExerciseMapping> storage = new HashSet<>();

            @Override
            public List<PracticeWordSetExerciseMapping> findByWordAndWordSetId(String word, int wordSetId) {
                LinkedList<PracticeWordSetExerciseMapping> result = new LinkedList<>();
                for (PracticeWordSetExerciseMapping mapping : storage) {
                    if (mapping.getWord().equals(word) && mapping.getWordSetId() == wordSetId) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public Dao.CreateOrUpdateStatus createNewOrUpdate(PracticeWordSetExerciseMapping exercise) {
                storage.add(exercise);
                return new Dao.CreateOrUpdateStatus(false, false, 0);
            }

            @Override
            public void cleanByWordSetId(int wordSetId) {
                Set<PracticeWordSetExerciseMapping> old = storage;
                storage = new HashSet<>();
                for (PracticeWordSetExerciseMapping mapping : old) {
                    if (mapping.getWordSetId() == wordSetId) {
                        continue;
                    }
                    storage.add(mapping);
                }
            }

            @Override
            public int createAll(List<PracticeWordSetExerciseMapping> words) {
                storage.addAll(words);
                return 0;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findByStatusAndByWordSetId(WordSetExperienceStatus status, int wordSetId) {
                LinkedList<PracticeWordSetExerciseMapping> result = new LinkedList<>();
                for (PracticeWordSetExerciseMapping mapping : storage) {
                    if (mapping.getStatus() == status && mapping.getWordSetId() == wordSetId) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public List<PracticeWordSetExerciseMapping> findByCurrentAndByWordSetId(int wordSetId) {
                LinkedList<PracticeWordSetExerciseMapping> result = new LinkedList<>();
                for (PracticeWordSetExerciseMapping mapping : storage) {
                    if (mapping.getWordSetId() == wordSetId && mapping.isCurrent()) {
                        result.add(mapping);
                    }
                }
                return result;
            }
        };
    }

    @Override
    public WordSetExperienceDao provideWordSetExperienceDao(DatabaseHelper databaseHelper) {
        return new WordSetExperienceDao() {
            private Set<WordSetExperienceMapping> storage = new HashSet<>();

            @Override
            public void createNewOrUpdate(WordSetExperienceMapping experience) {
                storage.add(experience);
            }

            @Override
            public WordSetExperienceMapping findById(int id) {
                for (WordSetExperienceMapping mapping : storage) {
                    if (mapping.getId() == id) {
                        return mapping;
                    }
                }
                return null;
            }
        };
    }
}