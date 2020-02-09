package talkapp.org.talkappmobile.model;

public enum RepetitionClass {
    NEW(0, 1),
    SEEN(1, 3),
    REPEATED(3, 7),
    LEARNED(7, Integer.MAX_VALUE);

    private final int from;
    private final int to;

    RepetitionClass(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public static RepetitionClass get(int repetitionCount) {
        for (RepetitionClass clazz : RepetitionClass.values()) {
            if (clazz.from <= repetitionCount && repetitionCount < clazz.to) {
                return clazz;
            }
        }
        return null;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }
}