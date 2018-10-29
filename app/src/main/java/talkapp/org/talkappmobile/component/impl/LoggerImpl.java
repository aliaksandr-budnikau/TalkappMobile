package talkapp.org.talkappmobile.component.impl;

import android.util.Log;

import talkapp.org.talkappmobile.component.Logger;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class LoggerImpl implements Logger {

    @Override
    public void i(String tag, String message, Object... args) {
        Log.i(tag, replacePlaceholders(message, args));
    }

    @Override
    public void w(String tag, String message, Object... args) {
        Log.w(tag, replacePlaceholders(message, args));
    }

    @Override
    public void e(String tag, String message, Object... args) {
        Log.e(tag, replacePlaceholders(message, args));
    }

    @Override
    public void e(String tag, Throwable throwable, String message, Object... args) {
        Log.e(tag, replacePlaceholders(message, args), throwable);
    }

    @Override
    public void d(String tag, String message, Object... args) {
        Log.d(tag, replacePlaceholders(message, args));
    }

    private String replacePlaceholders(String message, Object[] args) {
        if (isEmpty(message)) {
            return "";
        }
        for (Object arg : args) {
            message = message.replaceFirst("\\{\\}", String.valueOf(arg));
        }
        return message;
    }
}