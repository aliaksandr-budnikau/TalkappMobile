package talkapp.org.talkappmobile.dao;

import talkapp.org.talkappmobile.mappings.NewWordSetDraftMapping;

public interface NewWordSetDraftDao {
    NewWordSetDraftMapping getNewWordSetDraftById(int id);

    void createNewOrUpdate(NewWordSetDraftMapping mapping);
}