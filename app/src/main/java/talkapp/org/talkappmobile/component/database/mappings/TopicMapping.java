package talkapp.org.talkappmobile.component.database.mappings;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = TopicMapping.TOPIC_TABLE)
public class TopicMapping {
    public static final String TOPIC_TABLE = "Topic";
    public static final String ID_FN = "id";
    public static final String NAME_FN = "name";

    @DatabaseField(id = true, unique = true, canBeNull = false, columnName = ID_FN)
    private int id;

    @DatabaseField(canBeNull = false, columnName = NAME_FN)
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicMapping topic = (TopicMapping) o;
        return Objects.equals(name, topic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "TopicMapping{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}