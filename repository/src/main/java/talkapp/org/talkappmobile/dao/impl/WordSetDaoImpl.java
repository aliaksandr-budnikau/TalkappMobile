package talkapp.org.talkappmobile.dao.impl;


import com.j256.ormlite.dao.BaseDaoImpl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;

public class WordSetDaoImpl implements WordSetDao {

    private final BaseDaoImpl<WordSetMapping, String> dao;
    private Map<String, List<WordSetMapping>> wordSets = new HashMap<>();

    @Inject
    public WordSetDaoImpl(DatabaseHelper databaseHelper) {
        try {
            dao = new BaseDaoImpl<WordSetMapping, String>(databaseHelper.getConnectionSource(), WordSetMapping.class) {
            };
            dao.initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WordSetMapping> findAll() {
        if (wordSets != null && !wordSets.isEmpty()) {
            return getAllWordSets(wordSets);
        }
        List<WordSetMapping> mappings = null;
        try {
            mappings = dao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        wordSets = splitAllWortSetsByTopicId(mappings);
        return getAllWordSets(wordSets);
    }

    @Override
    public void refreshAll(List<WordSetMapping> mappings) {
        if (wordSets != null && !wordSets.isEmpty()) {
            return;
        }
        for (WordSetMapping mapping : mappings) {
            try {
                dao.createOrUpdate(mapping);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        wordSets = splitAllWortSetsByTopicId(mappings);
    }

    @Override
    public List<WordSetMapping> findAllByTopicId(String topicId) {
        findAll();
        return wordSets.get(topicId) == null ? Collections.<WordSetMapping>emptyList() : wordSets.get(topicId);
    }

    @Override
    public WordSetMapping findById(int id) {
        List<WordSetMapping> all = findAll();
        for (WordSetMapping wordSetMapping : all) {
            if (wordSetMapping.getId().equals(String.valueOf(id))) {
                return wordSetMapping;
            }
        }
        return null;
    }

    @Override
    public void createNewOrUpdate(WordSetMapping mapping) {
        try {
            dao.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        wordSets = new HashMap<>();
    }

    @Override
    public Integer getTheLastCustomWordSetsId() {
        try {
            String query = dao.queryBuilder().selectRaw("MAX(CAST( " + WordRepetitionProgressMapping.ID_FN + " as INTEGER))").prepareStatementString();
            String id = dao.queryRaw(query).getFirstResult()[0];
            if (id == null) {
                return null;
            }
            return Integer.parseInt(id);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void removeById(int id) {
        try {
            dao.deleteById(String.valueOf(id));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        wordSets = new HashMap<>();
    }

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
}