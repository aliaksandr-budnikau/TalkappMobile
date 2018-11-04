package talkapp.org.talkappmobile;

import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import talkapp.org.talkappmobile.activity.presenter.PresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.backend.impl.RegistrationException;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static java.util.Arrays.asList;
import static junit.framework.Assert.fail;

public class StressServerTest extends PresenterAndInteractorIntegTest implements Runnable {

    public static final Random RANDOM = new Random();
    private Set<String> badWords = new HashSet<>(asList(
            "camerawoman",
            "waterpark",
            "backpacking",
            "seaquake",
            "pinata",
            "starlet",
            "storyline",
            "frisbee",
            "petrol",
            "hardball",
            "ecoterrorism",
            "goodie",
            "biodiversity",
            "ecotourism",
            "catalytic",
            "waterski",
            "waterspout",
            "horse-trading",
            "subplot",
            "photoelectric",
            "floodwater",
            "actor-manager",
            "sundress",
            "july",
            "voice-over",
            "wingding",
            "aftershock",
            "AC",
            "superhero",
            "cameo",
            "conservancy",
            "cameo",
            "conservationist",
            "magnitude",
            "heart-throb",
            "leverage",
            "biofuel",
            "tidal",
            "stuntman",
            "epicentre",
            "tidal",
            "matinee",
            "environmentally",
            "DC",
            "temblor",
            "smog",
            "Gaia",
            "climatology",
            "breeder",
            "biodegradable",
            "environmental",
            "sunhat",
            "backstory",
            "stuntwoman",
            "sulphur",
            "film-goer",
            "camerawork",
            "megastar",
            "baddy",
            "low-impact",
            "biodegradable",
            "fictionalize",
            "cinematography",
            "unleaded",
            "swimsuit",
            "outdoor",
            "popsicle",
            "usherette",
            "Hollywood",
            "cupcake",
            "favors",
            "greenwash",
            "biohazard",
            "reef",
            "toy",
            "refinery",
            "gaffer",
            "rainforest",
            "particulate",
            "CFC",
            "non-biodegradable",
            "cataclysm",
            "feature-length",
            "airlift",
            "eco-friendly",
            "windstorm",
            "june",
            "zoris",
            "leach",
            "miscast",
            "plume",
            "colorize",
            "sandcastle"));

    //@Test
    public void stress() throws InterruptedException {
        LinkedList<Thread> threads = new LinkedList<>();
        for (int i = 0; i < 1400; i++) {
            Thread thread = new Thread(this);
            threads.add(thread);
            Thread.sleep(1000);
            thread.start();
        }
        long timeMillis = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("took -" + (System.currentTimeMillis() - timeMillis));
    }

    @Override
    public void run() {
        BackendServer server = getClassForInjection().getServer();
        String email = "sasha-ne@tut.by" + RANDOM.nextInt();
        String password = "password0";

        Account account = new Account();
        account.setEmail(email);
        account.setPassword(password);
        try {
            server.registerAccount(account);
        } catch (RegistrationException e) {
            fail();
        }

        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail(email);
        credentials.setPassword(password);
        try {
            server.loginUser(credentials);
        } catch (LoginException e) {
            fail();
        }

        while (RANDOM.nextInt(5) != 3) {

            List<Topic> topics = server.findAllTopics();
            if (topics.isEmpty()) {
                fail();
            }


            List<WordSet> sets = server.findAllWordSets();
            if (sets.isEmpty()) {
                fail();
            }

            WordSet wordSet = null;
            if (RANDOM.nextBoolean()) {
                wordSet = sets.get(RANDOM.nextInt(sets.size()));
            } else {
                Topic topic = topics.get(RANDOM.nextInt(topics.size()));
                List<WordSet> topicWordSets = server.findWordSetsByTopicId(topic.getId());
                if (topicWordSets.isEmpty()) {
                    fail();
                }
                wordSet = topicWordSets.get(RANDOM.nextInt(topicWordSets.size()));
            }

            if (wordSet == null) {
                fail();
            }

            List<WordTranslation> translations = server.findWordTranslationsByWordSetIdAndByLanguage(wordSet.getId(), "russian");
            if (translations == null) {
                fail();
            }

            List<String> words = wordSet.getWords();
            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                if (badWords.contains(word)) {
                    continue;
                }
                List<Sentence> sentences = server.findSentencesByWords(word, 6);
                if (sentences.isEmpty()) {
                    System.out.println("Sentences for word '" + word + "' were not found.");
                    fail();
                }
                Sentence sentence = sentences.get(RANDOM.nextInt(sentences.size()));
                if (RANDOM.nextBoolean()) {
                    List<GrammarError> errors = server.checkText(sentence.getText());
                    if (!errors.isEmpty()) {
                        fail();
                    }
                } else {
                    server.checkText(sentence.getText().replaceAll("the", "a"));
                    i--;
                }
            }
        }
    }
}