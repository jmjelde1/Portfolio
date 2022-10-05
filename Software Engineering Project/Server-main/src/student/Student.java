package student;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.Game;
import game.Story;
import utils.DatabaseStaticHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class Student {

	private int databaseStudentId;
	@JsonProperty("firstName")
	public final String firstName;
	@JsonProperty("lastName")
	public final String lastName;
	@JsonProperty("studentId")
	public final String schoolStudentId;
	public final int teacherId;
	private int storyIndex;
	@JsonProperty("solvedWords")
	private final List<Integer> solvedWords;
	private int score;
	private final Game game;

	public Student(final int databaseStudentId, final String firstName, final String lastName, final String schoolStudentId, final int teacherId, final int storyIndex, final Integer[] solvedWords, final int score, final Game game) {
		this.databaseStudentId = databaseStudentId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.schoolStudentId = schoolStudentId;
		this.teacherId = teacherId;
		this.storyIndex = storyIndex;
		this.solvedWords = new ArrayList<>(Arrays.asList(solvedWords));
		this.score = score;
		this.game = game;
	}

	public int getDatabaseStudentId() {
		return this.databaseStudentId;
	}

	public void setDatabaseStudentId(final int databaseStudentId) {
		this.databaseStudentId = databaseStudentId;
	}

	@JsonProperty("score")
	public int getScore() {
		return this.score;
	}

	@JsonProperty("gameStarted")
	public boolean gameStarted() {
		return this.game.hasStarted();
	}

	@JsonProperty("gameEnded")
	public boolean gameEnded() {
		return this.game.hasEnded() || this.game.studentIsFinished(this);
	}

	@JsonProperty("storyIndex")
	public int getStoryIndex() {
		return this.storyIndex;
	}

	@JsonProperty("story")
	public Story getStory() {
		return this.game.getStory(this.storyIndex);
	}

	public Integer[] getSolvedWords() {
		return this.solvedWords.toArray(new Integer[this.solvedWords.size()]);
	}

	public int getSolvedWordCount() {
		return this.solvedWords.size();
	}

	public boolean hasSolvedWord(final int solvableWordIndex) {
		return this.solvedWords.contains(solvableWordIndex);
	}

	public void addSolvedWordIndex(final Integer solvedWordIndex) {
		this.solvedWords.add(solvedWordIndex);
	}

	public void advanceStory() {
		this.storyIndex++;
		this.solvedWords.clear();
	}

	public boolean scoreSolution(final String studentSolution, final int solvableWordIndex) throws IndexOutOfBoundsException {
		return this.game.scoreSolution(this, studentSolution, solvableWordIndex);
	}

	public void addToScore(final int score) {
		this.score += score;
	}

	public Game getGame() {
		return this.game;
	}

}
