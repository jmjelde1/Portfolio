package game;

import java.util.Arrays;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Story {

	private int id;
	private final String solvedStory;
	private final String[] words;
	private final int wordCount;
	private final int storyCharLength;
	private int[] scrambledWordIndexes = new int[0];
	private String[] scrambledWords = new String[0];
	private int scrambledWordCount = 0;
	
	public Story(final int id, final String story) {
		this.id = id;
		this.solvedStory = story;
		this.words = story.split(" ");
		this.wordCount = words.length;
		this.storyCharLength = story.length();
	}
	
	public Story(final int id, final String story, final int[] scrambledWordIndexes) throws IndexOutOfBoundsException {
		this(id, story);
		this.scrambleWordsAt(scrambledWordIndexes);
	}

	public Story(final int id, final String story, final String scrambledWordIndexesAsString) {
		this(id, story);
		final String[] scrambledWordIndexesAsStringSplit = scrambledWordIndexesAsString.split(",");
		final int scrambledWordCount = scrambledWordIndexesAsStringSplit.length;
		int[] scrambledWordIndexes = new int[scrambledWordCount];
		for(int solvableWordIndex = 0; solvableWordIndex < scrambledWordCount; solvableWordIndex++)
			scrambledWordIndexes[solvableWordIndex] = Integer.parseInt(scrambledWordIndexesAsStringSplit[solvableWordIndex]);
		this.scrambleWordsAt(scrambledWordIndexes);
	}

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}
	
	public void scrambleWordsAt(final int[] scrambledWordIndexes) throws IndexOutOfBoundsException {
		Arrays.sort(scrambledWordIndexes); // getUnsolvedStory needs this to be in ascending order.
		final int wordCount = this.wordCount, scrambledWordCount = scrambledWordIndexes.length;
		String[] scrambledWords = new String[scrambledWordCount];
		for(int solvableWordIndex = 0; solvableWordIndex < scrambledWordCount; solvableWordIndex++) {
			final int wordIndex = scrambledWordIndexes[solvableWordIndex];
			if(wordIndex < 0 || wordIndex >= wordCount) {
				throw new IndexOutOfBoundsException("scrambleWordsAt: Invalid word index of " + wordIndex + '!');
			}
			scrambledWords[solvableWordIndex] = scramble(this.words[wordIndex]);
		}
		this.scrambledWordIndexes = scrambledWordIndexes;
		this.scrambledWords = scrambledWords;
		this.scrambledWordCount = scrambledWordCount;
	}
	
	public String getUnsolvedWordBySolvableIndex(final int solvableWordIndex) throws IndexOutOfBoundsException {
		final int scrambledWordCount = this.scrambledWordCount;
		if(solvableWordIndex < 0 || solvableWordIndex >= scrambledWordCount) {
			throw new IndexOutOfBoundsException("getUnsolvedWordBySolvableIndex: Invalid solvable word index of " + solvableWordIndex + '!');
		}
		return this.scrambledWords[solvableWordIndex];
	}
	
	public String getSolvedWordBySolvableIndex(final int solvableWordIndex) throws IndexOutOfBoundsException {
		final int scrambledWordCount = this.scrambledWordCount;
		if(solvableWordIndex < 0 || solvableWordIndex >= scrambledWordCount) {
			throw new IndexOutOfBoundsException("getSolvedWordBySolvableIndex: Invalid solvable word index of " + solvableWordIndex + '!');
		}
		return this.words[this.scrambledWordIndexes[solvableWordIndex]];
	}
	
	public int scoreSolution(final String solution, final int solvableWordIndex) throws IndexOutOfBoundsException {
		return this.getSolvedWordBySolvableIndex(solvableWordIndex).equals(solution) ? 1 : 0;
	}
	
	@JsonProperty("unsolvedStory")
	public String getUnsolvedStory() {
		final int scrambledWordCount = this.scrambledWordCount;
		if(scrambledWordCount == 0)
			return this.getSolvedStory();
		final int wordCount = this.wordCount;
		final StringBuilder s = new StringBuilder(this.storyCharLength);
		int solvableWordIndex = 0;
		int scrambledWordIndex = this.scrambledWordIndexes[solvableWordIndex];
		String word;
		for(int wordIndex = 0; wordIndex < wordCount; wordIndex++) {
			if(scrambledWordIndex == wordIndex) {
				word = this.scrambledWords[solvableWordIndex];
				if(++solvableWordIndex >= scrambledWordCount)
					scrambledWordIndex = -1;
				else scrambledWordIndex = this.scrambledWordIndexes[solvableWordIndex];
			} else word = this.words[wordIndex];
			s.append(wordIndex == wordCount - 1 ? word : word + ' ');
		}
		return s.toString();
	}
	
	@JsonProperty("solvedStory")
	public String getSolvedStory() {
		return this.solvedStory;
	}
	
	@JsonProperty("solvableWordIndexes")
	public int[] getScrambledWordIndexes() {
		return this.scrambledWordIndexes;
	}
	
	private static String scramble(final String word) {
		int len = word.length();
		char[] parse = new char[len];
		int[] index = new int[len]; 
		while(true) {
			String ret = "";
			word.getChars(0, len, parse,0);
			Random rand = new Random();
			for(int i = 0; i<len; i++) {
				index[i] = -1;
			}
			for(int i = 0; i<len; i++) {
				int random = rand.nextInt(len);
				while(index[random] != -1 ) {
					random = rand.nextInt(len);
				}
				index[random] = i; 
			}
			for(int i = 0; i<len; i++) {
				ret = ret + parse[index[i]];
			}
			if(!(word.equals(ret)))
				return ret;
		}
	}
	
}
