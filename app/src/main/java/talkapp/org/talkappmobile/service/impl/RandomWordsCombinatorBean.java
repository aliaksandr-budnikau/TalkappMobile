package talkapp.org.talkappmobile.service.impl;

import org.androidannotations.annotations.EBean;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.service.WordsCombinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Budnikau Aliaksandr
 */
@EBean(scope = EBean.Scope.Singleton)
public class RandomWordsCombinatorBean implements WordsCombinator {

    @Override
    public Set<Word2Tokens> combineWords(List<Word2Tokens> words) {
        if (words.isEmpty()) {
            throw new IllegalArgumentException("The list of words is empty");
        }
        ArrayList<Word2Tokens> sequence = new ArrayList<>(words);
        Collections.shuffle(sequence);
        return new LinkedHashSet<>(sequence);
    }
}
