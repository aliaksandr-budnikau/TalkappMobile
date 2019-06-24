package org.talkappmobile.events;

import org.talkappmobile.model.Word2Tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class OriginalTextClickEM {
    @NonNull
    private Word2Tokens word;
}