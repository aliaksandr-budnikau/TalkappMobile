package talkapp.org.talkappmobile.service;

import talkapp.org.talkappmobile.model.Word2Tokens;

import java.util.List;
import java.util.Set;

/**
 * @author Budnikau Aliaksandr
 */
@Deprecated
public interface WordsCombinator {

    Set<Word2Tokens> combineWords(List<Word2Tokens> words);
}
