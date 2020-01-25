package talkapp.org.talkappmobile.service;

import java.util.List;

import talkapp.org.talkappmobile.model.WordSet;

public interface WordSetRepository {
    List<WordSet> findAll();

    List<WordSet> findAllByTopicId(int topicId);
}
