package talkapp.org.talkappmobile.component;

import java.util.HashMap;

/**
 * @author Budnikau Aliaksandr
 */
public class AuthSign extends HashMap<String, String> {

    public static final String AUTHORIZATION_HEADER_KEY = "Authorization";

    public void put(String signature) {
        this.put(AUTHORIZATION_HEADER_KEY, signature);
    }
}
