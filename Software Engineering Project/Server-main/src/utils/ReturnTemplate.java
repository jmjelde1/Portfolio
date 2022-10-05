package utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class ReturnTemplate<T> {

    interface AccountServerReturnProcessor<T> {
        T processRequest();
    }

    private final List<String> problems;
    private T result;

    ReturnTemplate() {
        this.problems = new LinkedList<>();
    }

    ReturnTemplate<T> validateAndProcessRequest(final InputValidator[] inputValidators, final AccountServerReturnProcessor<T> processor) {
        boolean success = true;
        for(final InputValidator inputValidator : inputValidators)
            success = inputValidator.validate(this.problems) && success;
        if (success)
            this.result = processor.processRequest();
        return this;
    }

    @JsonProperty("problems")
    public List<String> getProblems() {
        return this.problems;
    }

    @JsonProperty("result")
    public T getResult() {
        return this.result;
    }

}
