package org.talkappmobile.service;

import org.talkappmobile.model.Word2Tokens;

import java.util.List;
import java.util.Set;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordsCombinator {

    Set<Word2Tokens> combineWords(List<Word2Tokens> words);
}
