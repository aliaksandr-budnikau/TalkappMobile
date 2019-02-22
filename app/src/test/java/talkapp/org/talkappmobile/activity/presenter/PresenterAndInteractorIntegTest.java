package talkapp.org.talkappmobile.activity.presenter;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.powermock.reflect.Whitebox;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import talkapp.org.talkappmobile.component.backend.DataServer;
import talkapp.org.talkappmobile.component.backend.impl.BackendServerFactoryBean;
import talkapp.org.talkappmobile.component.backend.impl.RequestExecutor;
import talkapp.org.talkappmobile.component.database.LocalDataService;
import talkapp.org.talkappmobile.component.database.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.component.database.dao.SentenceDao;
import talkapp.org.talkappmobile.component.database.dao.TopicDao;
import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.dao.WordTranslationDao;
import talkapp.org.talkappmobile.component.database.impl.LocalDataServiceImpl;
import talkapp.org.talkappmobile.component.database.impl.ServiceFactoryBean;
import talkapp.org.talkappmobile.component.database.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.SentenceMapping;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;
import talkapp.org.talkappmobile.component.impl.LoggerBean;
import talkapp.org.talkappmobile.model.WordSetProgressStatus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class PresenterAndInteractorIntegTest {

    private DataServer server;
    private WordSetDao wordSetDao;
    private SentenceDao sentenceDao;

    {
        BackendServerFactoryBean factory = new BackendServerFactoryBean();
        Whitebox.setInternalState(factory, "logger", new LoggerBean());
        ServiceFactoryBean mockServiceFactoryBean = mock(ServiceFactoryBean.class);
        when(mockServiceFactoryBean.getLocalDataService()).thenReturn(provideLocalDataService());
        Whitebox.setInternalState(factory, "serviceFactory", mockServiceFactoryBean);
        Whitebox.setInternalState(factory, "requestExecutor", new RequestExecutor());
        server = factory.get();
    }

    private LocalDataService provideLocalDataService() {
        return new LocalDataServiceImpl(provideWordSetDao(), mock(TopicDao.class), provideSentenceDao(), mock(WordTranslationDao.class), new ObjectMapper(), new LoggerBean());
    }

    protected WordSetDao provideWordSetDao() {
        if (wordSetDao == null) {
            wordSetDao = createWordSetDao();
        }
        return wordSetDao;
    }

    protected SentenceDao provideSentenceDao() {
        if (sentenceDao == null) {
            sentenceDao = createSentenceDao();
        }
        return sentenceDao;
    }

    private WordSetDao createWordSetDao() {
        return new WordSetDao() {

            private Map<String, List<WordSetMapping>> wordSets = new HashMap<>();

            @Override
            public List<WordSetMapping> findAll() {
                return getAllWordSets(wordSets);
            }

            @Override
            public void save(List<WordSetMapping> mappings) {
                wordSets = splitAllWortSetsByTopicId(mappings);
            }

            @Override
            public List<WordSetMapping> findAllByTopicId(String topicId) {
                return wordSets.get(topicId) == null ? new LinkedList<WordSetMapping>() : wordSets.get(topicId);
            }

            @Override
            public WordSetMapping findById(int id) {
                List<WordSetMapping> all = getAllWordSets(wordSets);
                for (WordSetMapping wordSetMapping : all) {
                    if (wordSetMapping.getId().equals(String.valueOf(id))) {
                        return wordSetMapping;
                    }
                }
                return null;
            }

            @Override
            public void createNewOrUpdate(WordSetMapping wordSetMapping) {
                WordSetMapping mapping = findById(Integer.parseInt(wordSetMapping.getId()));
                if (mapping == null) {
                    throw new RuntimeException("not implemented");
                } else {
                    mapping.setTrainingExperience(wordSetMapping.getTrainingExperience());
                    mapping.setStatus(wordSetMapping.getStatus());
                    mapping.setWords(wordSetMapping.getWords());
                    //mapping.setId(wordSetMapping.getId());
                    mapping.setTop(wordSetMapping.getTop());
                    mapping.setTopicId(wordSetMapping.getTopicId());
                }
            }

            @NonNull
            private List<WordSetMapping> getAllWordSets(Map<String, List<WordSetMapping>> all) {
                LinkedList<WordSetMapping> result = new LinkedList<>();
                for (List<WordSetMapping> wordSets : all.values()) {
                    result.addAll(wordSets);
                }
                return result;
            }

            private Map<String, List<WordSetMapping>> splitAllWortSetsByTopicId(List<WordSetMapping> incomminMapping) {
                Map<String, List<WordSetMapping>> result = new HashMap<>();
                for (WordSetMapping mapping : incomminMapping) {
                    List<WordSetMapping> wordSetList = result.get(mapping.getTopicId());
                    if (wordSetList == null) {
                        wordSetList = new LinkedList<>();
                        result.put(mapping.getTopicId(), wordSetList);
                    }
                    wordSetList.add(mapping);
                }
                return result;
            }
        };
    }

    private SentenceDao createSentenceDao() {
        return new SentenceDao() {

            private Map<String, List<SentenceMapping>> sentences = new HashMap<>();

            @Override
            public void save(List<SentenceMapping> mappings) {
                for (SentenceMapping mapping : mappings) {
                    String[] ids = mapping.getId().split("#");
                    List<SentenceMapping> list = sentences.get(getKey(ids[1], Integer.valueOf(ids[2])));
                    if (list != null && !list.isEmpty()) {
                        continue;
                    } else {
                        sentences.put(getKey(ids[1], Integer.valueOf(ids[2])), new LinkedList<SentenceMapping>());
                    }
                    sentences.get(getKey(ids[1], Integer.valueOf(ids[2]))).add(mapping);
                }
            }

            @Override
            public List<SentenceMapping> findAllByWord(String word, int wordsNumber) {
                List<SentenceMapping> mappings = sentences.get(getKey(word, wordsNumber));
                return mappings == null ? new LinkedList<SentenceMapping>() : mappings;
            }

            @Override
            public SentenceMapping findById(String id) {
                for (List<SentenceMapping> list : sentences.values()) {
                    for (SentenceMapping sentenceMapping : list) {
                        if (sentenceMapping.getId().startsWith(id)) {
                            return sentenceMapping;
                        }
                    }
                }
                return null;
            }

            private String getKey(String word, int wordsNumber) {
                return word + "_" + wordsNumber;
            }
        };
    }

    public DataServer getServer() {
        return server;
    }

    protected WordRepetitionProgressDao provideWordRepetitionProgressDao() {
        return new WordRepetitionProgressDao() {
            private Set<WordRepetitionProgressMapping> storage = new HashSet<>();

            @Override
            public List<WordRepetitionProgressMapping> findByWordAndWordSetId(String word, int wordSetId) {
                LinkedList<WordRepetitionProgressMapping> result = new LinkedList<>();
                for (WordRepetitionProgressMapping mapping : storage) {
                    if (mapping.getWordJSON().equals(word) && mapping.getWordSetId() == wordSetId) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public void createNewOrUpdate(WordRepetitionProgressMapping exercise) {
                storage.add(exercise);
            }

            @Override
            public void cleanByWordSetId(int wordSetId) {
                Set<WordRepetitionProgressMapping> old = storage;
                storage = new HashSet<>();
                for (WordRepetitionProgressMapping mapping : old) {
                    if (mapping.getWordSetId() == wordSetId) {
                        continue;
                    }
                    storage.add(mapping);
                }
            }

            @Override
            public int createAll(List<WordRepetitionProgressMapping> words) {
                storage.addAll(words);
                return 0;
            }

            @Override
            public List<WordRepetitionProgressMapping> findByStatusAndByWordSetId(WordSetProgressStatus status, int wordSetId) {
                LinkedList<WordRepetitionProgressMapping> result = new LinkedList<>();
                for (WordRepetitionProgressMapping mapping : storage) {
                    if (mapping.getStatus() == status && mapping.getWordSetId() == wordSetId) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public List<WordRepetitionProgressMapping> findByCurrentAndByWordSetId(int wordSetId) {
                LinkedList<WordRepetitionProgressMapping> result = new LinkedList<>();
                for (WordRepetitionProgressMapping mapping : storage) {
                    if (mapping.getWordSetId() == wordSetId && mapping.isCurrent()) {
                        result.add(mapping);
                    }
                }
                return result;
            }

            @Override
            public List<WordRepetitionProgressMapping> findFinishedWordSetsSortByUpdatedDate(long limit, Date olderThenInHours) {
                return null;
            }

            @Override
            public List<WordRepetitionProgressMapping> findByWordAndByStatus(String word, WordSetProgressStatus status) {
                return null;
            }

            @Override
            public List<WordRepetitionProgressMapping> findByWordAndBySentenceAndByStatus(String s, String s1, WordSetProgressStatus finished) {
                return null;
            }
        };
    }
}