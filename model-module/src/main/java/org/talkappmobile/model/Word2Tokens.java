package org.talkappmobile.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Word2Tokens implements Serializable {
    @NonNull
    private String word;
    @NonNull
    private String tokens;
    @NonNull
    private Integer sourceWordSetId;
}