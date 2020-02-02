package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.Sentence;

public interface SentenceRepository {

    void createNewOrUpdate(Sentence sentence);

    Sentence findById(String id);
}