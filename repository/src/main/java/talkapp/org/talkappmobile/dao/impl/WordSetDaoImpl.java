package talkapp.org.talkappmobile.dao.impl;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.mappings.WordRepetitionProgressMapping;
import talkapp.org.talkappmobile.mappings.WordSetMapping;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WordSetDaoImpl extends BaseDaoImpl<WordSetMapping, String> implements WordSetDao {

    private Map<String, List<WordSetMapping>> wordSets = new HashMap<>();

    public WordSetDaoImpl(ConnectionSource connectionSource, Class<WordSetMapping> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    @Override
    public List<WordSetMapping> findAll() {
        if (wordSets != null && !wordSets.isEmpty()) {
            return getAllWordSets(wordSets);
        }
        List<WordSetMapping> mappings = null;
        try {
            mappings = this.queryForAll();
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
                this.createOrUpdate(mapping);
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
            this.createOrUpdate(mapping);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        wordSets = new HashMap<>();
    }

    @Override
    public Integer getTheLastCustomWordSetsId() {
        try {
            String query = queryBuilder().selectRaw("MAX(CAST( " + WordRepetitionProgressMapping.ID_FN + " as INTEGER))").prepareStatementString();
            String id = this.queryRaw(query).getFirstResult()[0];
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
            this.deleteById(String.valueOf(id));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        wordSets = new HashMap<>();
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
}