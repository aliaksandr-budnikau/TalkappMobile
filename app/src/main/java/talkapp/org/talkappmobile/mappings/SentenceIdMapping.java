package talkapp.org.talkappmobile.mappings;

@Deprecated
public class SentenceIdMapping {
    private String sentenceId;
    private String word;
    private int lengthInWords;

    public SentenceIdMapping() {
    }

    public SentenceIdMapping(String sentenceId, int lengthInWords) {
        this.sentenceId = sentenceId;
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
}