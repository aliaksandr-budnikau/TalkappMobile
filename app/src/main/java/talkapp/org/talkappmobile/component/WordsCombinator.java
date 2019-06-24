package talkapp.org.talkappmobile.component;

import java.util.List;
import java.util.Set;

import org.talkappmobile.model.Word2Tokens;

/**
 * @author Budnikau Aliaksandr
 */
public interface WordsCombinator {

    Set<Word2Tokens> combineWords(List<Word2Tokens> words);
}
