package org.talkappmobile.events;

import org.talkappmobile.model.WordSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class WordSetsRemoveClickedEM {
    @NonNull
    private WordSet wordSet;
    @NonNull
    private int clickedItemNumber;
}
