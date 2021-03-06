package talkapp.org.talkappmobile.component.database.dao.impl.local;

import android.support.annotation.NonNull;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import talkapp.org.talkappmobile.component.database.dao.WordSetDao;
import talkapp.org.talkappmobile.component.database.mappings.local.WordSetMapping;

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
    public void save(List<WordSetMapping> mappings) {
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
        return wordSets.get(topicId);
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