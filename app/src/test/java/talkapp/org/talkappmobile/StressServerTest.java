package talkapp.org.talkappmobile;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

import talkapp.org.talkappmobile.activity.presenter.PresenterAndInteractorIntegTest;
import talkapp.org.talkappmobile.component.backend.BackendServer;
import talkapp.org.talkappmobile.component.backend.impl.LoginException;
import talkapp.org.talkappmobile.component.backend.impl.RegistrationException;
import talkapp.org.talkappmobile.model.Account;
import talkapp.org.talkappmobile.model.GrammarError;
import talkapp.org.talkappmobile.model.LoginCredentials;
import talkapp.org.talkappmobile.model.Sentence;
import talkapp.org.talkappmobile.model.Topic;
import talkapp.org.talkappmobile.model.Word2Tokens;
import talkapp.org.talkappmobile.model.WordSet;
import talkapp.org.talkappmobile.model.WordTranslation;

import static junit.framework.Assert.fail;

public class StressServerTest extends PresenterAndInteractorIntegTest implements Runnable {

    public static final Random RANDOM = new Random();
    private Set<Word2Tokens> badWords = new ConcurrentSkipListSet<>();
    private AtomicInteger numberThreadsInWork = new AtomicInteger();

    //@Test
    public void stress() throws InterruptedException {
        LinkedList<Thread> threads = new LinkedList<>();
        for (int i = 0; i < 14000; i++) {
            Thread thread = new Thread(this);
            threads.add(thread);
            Thread.sleep(30);
            thread.start();
            for (Word2Tokens word : badWords) {
                System.out.print("\"" + word + "\", ");
            }
            System.out.println();
        }
        long timeMillis = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("took -" + (System.currentTimeMillis() - timeMillis));
    }

    @Override
    public void run() {
        try {
            System.out.println(numberThreadsInWork.incrementAndGet());
            doActivity();
        } finally {
            System.out.println(numberThreadsInWork.decrementAndGet());
        }
    }

    private void doActivity() {
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

            List<Word2Tokens> words = wordSet.getWords();
            for (int i = 0; i < words.size(); i++) {
                Word2Tokens word = words.get(i);
                if (badWords.contains(word)) {
                    continue;
                }
                List<Sentence> sentences = server.findSentencesByWords(word, 6);
                if (sentences.isEmpty()) {
                    badWords.add(word);
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