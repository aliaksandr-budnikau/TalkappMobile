package talkapp.org.talkappmobile.mappings;

public class SentenceIdMapping {
    private String sentenceId;
    @Deprecated
    private String word;
    private int lengthInWords;

    public SentenceIdMapping() {
    }

    public SentenceIdMapping(String sentenceId, int lengthInWords) {
        this.sentenceId = sentenceId;
        this.lengthInWords = lengthInWords;
    }

    @Deprecated
    public SentenceIdMapping(String sentenceId, @Deprecated String word, int lengthInWords) {
        this.sentenceId = sentenceId;
        this.word = word;
        this.lengthInWords = lengthInWords;
    }

    public String getSentenceId() {
        return sentenceId;
    }

    @Deprecated
    public String getWord() {
        return word;
    }

    @Deprecated
    public void setWord(String word) {
        this.word = word;
    }

    public int getLengthInWords() {
        return lengthInWords;
    }

    @Override
    public String toString() {
        return sentenceId + "#" + word + "#" + lengthInWords;
    }
}