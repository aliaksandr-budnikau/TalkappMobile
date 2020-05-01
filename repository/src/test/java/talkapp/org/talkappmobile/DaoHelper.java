package talkapp.org.talkappmobile;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.robolectric.RuntimeEnvironment;

import java.sql.SQLException;

import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.dao.ExpAuditDao;
import talkapp.org.talkappmobile.dao.NewWordSetDraftDao;
import talkapp.org.talkappmobile.dao.SentenceDao;
import talkapp.org.talkappmobile.dao.WordRepetitionProgressDao;
import talkapp.org.talkappmobile.dao.WordSetDao;
import talkapp.org.talkappmobile.dao.WordTranslationDao;
import talkapp.org.talkappmobile.dao.impl.ExpAuditDaoImpl;
import talkapp.org.talkappmobile.dao.impl.NewWordSetDraftDaoImpl;
import talkapp.org.talkappmobile.dao.impl.SentenceDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordRepetitionProgressDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordSetDaoImpl;
import talkapp.org.talkappmobile.dao.impl.WordTranslationDaoImpl;

import static com.j256.ormlite.android.apptools.OpenHelperManager.getHelper;

public class DaoHelper {

    private final DatabaseHelper databaseHelper;
    private ExpAuditDaoImpl expAuditDao;
    private NewWordSetDraftDaoImpl newWordSetDraftDao;
    private WordSetDaoImpl wordSetDao;
    private SentenceDaoImpl sentenceDao;
    private WordTranslationDaoImpl wordTranslationDao;
    private WordRepetitionProgressDaoImpl repetitionProgressDao;

    public DaoHelper() {
        this.databaseHelper = getHelper(RuntimeEnvironment.application, DatabaseHelper.class);
    }

    public synchronized WordRepetitionProgressDao getWordRepetitionProgressDao() throws SQLException {
        if (repetitionProgressDao == null) {
            repetitionProgressDao = new WordRepetitionProgressDaoImpl(databaseHelper);
        }
        return repetitionProgressDao;
    }

    public synchronized WordTranslationDao getWordTranslationDao() throws SQLException {
        if (wordTranslationDao == null) {
            wordTranslationDao = new WordTranslationDaoImpl(databaseHelper);
        }
        return wordTranslationDao;
    }

    public synchronized SentenceDao getSentenceDao() throws SQLException {
        if (sentenceDao == null) {
            sentenceDao = new SentenceDaoImpl(databaseHelper);
        }
        return sentenceDao;
    }

    public synchronized WordSetDao getWordSetDao() throws SQLException {
        if (wordSetDao == null) {
            wordSetDao = new WordSetDaoImpl(databaseHelper);
        }
        return wordSetDao;
    }

    public synchronized NewWordSetDraftDao getNewWordSetDraftDao() throws SQLException {
        if (newWordSetDraftDao == null) {
            newWordSetDraftDao = new NewWordSetDraftDaoImpl(databaseHelper);
        }
        return newWordSetDraftDao;
    }

    public synchronized ExpAuditDao getExpAuditDao() throws SQLException {
        if (expAuditDao == null) {
            expAuditDao = new ExpAuditDaoImpl(databaseHelper);
        }
        return expAuditDao;
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public void releaseHelper() {
        OpenHelperManager.releaseHelper();
    }
}