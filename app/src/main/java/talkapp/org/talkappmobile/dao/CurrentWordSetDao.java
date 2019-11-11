package talkapp.org.talkappmobile.dao;

import talkapp.org.talkappmobile.mappings.CurrentWordSetMapping;

public interface CurrentWordSetDao {
    CurrentWordSetMapping getById(String id);

    void save(CurrentWordSetMapping mapping);
}