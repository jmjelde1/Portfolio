package utils;

import java.util.List;

public class InputValidator {

    private final String value;

    public InputValidator(final String value) {
        this.value = value;
    }

    public boolean validate(final List<String> problems) {
        if (this.isBlank()) {
            problems.add(this.getBlankInputProblem());
        } else if (this.isInvalid()) {
            problems.add(this.getInvalidInputProblem());
        } else return true;
        return false;
    }

    public boolean isBlank() {
        return this.getValue() == null || this.getValue().length() == 0;
    }

    public boolean isInvalid() {
        return false;
    }

    public String getBlankInputProblem() {
        return "INPUT_BLANK";
    }

    public String getInvalidInputProblem() {
        return "INPUT_INVALID";
    }

    public String getValue() {
        return this.value;
    }
}
