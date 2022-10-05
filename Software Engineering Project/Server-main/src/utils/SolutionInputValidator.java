package utils;

import game.Game;
import student.Student;

import java.util.List;

public class SolutionInputValidator extends InputValidator {

    private final int solvableWordIndex;
    private final Student student;

    SolutionInputValidator(final String input, final int solvableWordIndex, final Student student) {
        super(input);
        this.solvableWordIndex = solvableWordIndex;
        this.student = student;
    }

    @Override
    public boolean validate(final List<String> problems) {
        if (this.student == null) {
            problems.add("INVALID_STUDENT_ID");
            return false;
        }
        final Game game = this.student.getGame();
        if(!game.isInProgress())
            problems.add(game.hasStarted() ? "GAME_OVER" : "GAME_NOT_STARTED");
        else if (super.validate(problems)) {
            if (this.student.hasSolvedWord(this.solvableWordIndex))
                problems.add("ALREADY_SOLVED_WORD");
            else {
                try {
                    if (this.student.scoreSolution(this.getValue(), this.solvableWordIndex))
                        return true;
                    problems.add(this.getInvalidInputProblem());
                } catch (IndexOutOfBoundsException e) {
                    problems.add("INVALID_WORD");
                }
            }
        }
        return false;
    }

    @Override
    public String getBlankInputProblem() {
        return "SOLUTION_BLANK";
    }

    @Override
    public String getInvalidInputProblem() {
        return "SOLUTION_INCORRECT";
    }

}
