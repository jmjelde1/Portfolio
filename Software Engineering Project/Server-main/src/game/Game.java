package game;
import com.fasterxml.jackson.annotation.JsonProperty;
import student.Student;
import utils.DatabaseStaticHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

	public final int gameId;
	private List<Integer> storyIds;
	@JsonProperty("stories")
	private final List<Story> stories;
	@JsonProperty("started")
	private boolean started;
	@JsonProperty("ended")
	private boolean ended;

	public Game(final int gameId, final Integer[] storyIds, final boolean started, final boolean ended) {
		this.gameId = gameId;
		this.storyIds = new ArrayList<>(Arrays.asList(storyIds));
		this.stories = new ArrayList<>(storyIds.length);
		for(final Integer storyId : storyIds)
			this.stories.add(DatabaseStaticHandler.getStory(storyId));
		this.started = started;
	}

	public Story getStory(final int storyIndex) {
		return this.stories.get(storyIndex);
	}

	public boolean hasStarted() {
		return this.started;
	}
	public boolean hasEnded() {
		return this.ended;
	}
	public boolean isInProgress() {
		return this.hasStarted() && !this.hasEnded();
	}

	public Game start() {
		this.started = true;
		this.ended = false;
		return this;
	}
	public Game end() {
		if(!this.started)
			return null;
		this.ended = true;
		return this;
	}

	public Game reset() {
		this.started = this.ended = false;
		return this;
	}

	public boolean isLastStory(final int studentStoryIndex) {
		return studentStoryIndex >= this.stories.size() - 1;
	}

	public boolean storyIsFinished(final int studentStoryIndex, final int studentSolvedWordCount) {
		return studentSolvedWordCount >= this.getStory(studentStoryIndex).getScrambledWordIndexes().length;
	}

	public boolean studentIsFinished(final Student student) {
		return this.isLastStory(student.getStoryIndex()) && this.storyIsFinished(student.getStoryIndex(), student.getSolvedWordCount());
	}

	public boolean scoreSolution(final Student student, final String studentSolution, final int solvableWordIndex) throws IndexOutOfBoundsException {
		final int studentStoryIndex = student.getStoryIndex();
		final int scoreAdd = this.getStory(studentStoryIndex).scoreSolution(studentSolution, solvableWordIndex);
		if(scoreAdd > 0){
			student.addToScore(scoreAdd);
			student.addSolvedWordIndex(solvableWordIndex);
			if(this.storyIsFinished(studentStoryIndex, student.getSolvedWordCount()) && !this.isLastStory(studentStoryIndex))
				student.advanceStory();
			return true;
		}
		return false;
	}

	public void addStory(final Story story) {
		this.storyIds.add(story.getId());
		this.stories.add(story);
	}

	public Integer[] getStoryIds() {
		return this.storyIds.toArray(new Integer[this.storyIds.size()]);
	}

}
