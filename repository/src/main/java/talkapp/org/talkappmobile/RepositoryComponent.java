package talkapp.org.talkappmobile;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Component;
import talkapp.org.talkappmobile.dao.DatabaseHelper;
import talkapp.org.talkappmobile.repository.RepositoryFactoryImpl;

@Singleton
@Component(modules = {RepositoryModule.class, RepositoryBindModule.class})
public interface RepositoryComponent {

    DatabaseHelper databaseHelper();

    ObjectMapper mapper();

    void inject(RepositoryFactoryImpl target);
}