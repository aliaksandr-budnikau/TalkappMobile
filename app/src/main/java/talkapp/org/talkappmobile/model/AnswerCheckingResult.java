package talkapp.org.talkappmobile.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Budnikau Aliaksandr
 */
public class AnswerCheckingResult {
    private int currentTrainingExperience;

    private List<GrammarError> errors = new ArrayList<>();

    public int getCurrentTrainingExperience() {
        return currentTrainingExperience;
    }

    public void setCurrentTrainingExperience(int currentTrainingExperience) {
        this.currentTrainingExperience = currentTrainingExperience;
    }

    public List<GrammarError> getErrors() {
        return errors;
    }

    public void setErrors(List<GrammarError> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnswerCheckingResult that = (AnswerCheckingResult) o;
        return currentTrainingExperience == that.currentTrainingExperience &&
                Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTrainingExperience, errors);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnswerCheckingResult{");
        sb.append("currentTrainingExperience=").append(currentTrainingExperience);
        sb.append(", errors=").append(errors);
        sb.append('}');
        return sb.toString();
    }
}
