package talkapp.org.talkappmobile.component;

public interface Logger {
    void w(String tag, String message, Object... args);

    void e(String tag, String message, Object... args);

    void d(String tag, String message, Object... args);
}