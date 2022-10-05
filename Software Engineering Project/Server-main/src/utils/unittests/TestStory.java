package utils.unittests;

import game.Story;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


class TestStory {
	
	//Tests scrambleWordsAt method
	@Test
	public void testScrambleWordsAt() {
		String story = "this little piggy went to market";
		int[] index = {0,1,2,3,4,5};
		
		
		
		Story s = new Story(0, story);
		s.scrambleWordsAt(index);
		

		String answer = s.getUnsolvedStory();
		
		String word = answer.substring(0,4);
		//System.out.print(word);
		
		assertFalse(word.equals("this"));
		
		
		word = answer.substring(5,11);
	
		assertFalse(word.equals("little"));
		
		word = answer.substring(12,17);
		
		assertFalse(word.equals("piggy"));
		
		word = answer.substring(18,22);
	
		assertTrue(word.equals("went"));
		
		word = answer.substring(23,25);
		assertTrue(word.equals("to"));
		
		word = answer.substring(26,32);
		assertTrue(word.equals("market"));
	
		
	}
	


		
	//Tests scoreSolution method
	@Test
	public void testScoreSolution() {
		String story = "this little piggy went to market";
		int[] index = {0,1,2};
		Story s = new Story(0, story, index);

		assertTrue(s.scoreSolution("this", 0)==1);
		assertFalse(s.scoreSolution("shit", 0)==1);
		assertTrue(s.scoreSolution("piggy", 2)==1);
		assertTrue(s.scoreSolution("this", 0)==1);
		
		
	}
	
	//Tests getUnsolvedStory method
	@Test
	public void testGetUnsolvedStory() {
		String story = "this little piggy went to market";
		int[] index = {0,1,2,3,4,5};
		Story s = new Story(0, story, index);
		// s.getUnsolvedStory();
		
		String unsolvedStory1 = s.getUnsolvedStory();
		assertFalse(unsolvedStory1.equals(story));
		System.out.println("Unsolved story 1: " + unsolvedStory1);
		
		
		//Fails when index  does not start from 0 and is not in order
		 story = "this little piggy went to market";
		int[] index2 = {3, 1};
		 s = new Story(0, story, index2);
		String unsolvedStory2 = s.getUnsolvedStory();
		assertFalse(unsolvedStory2.equals(story));
		System.out.println("Unsolved story 2: " + unsolvedStory2);
	}
	
	//Test getSolvedStory method
	@Test
	public void testGetSolvedStory() {
		String story = "this little piggy went to market";
		int[] index = {0,1,2,3,4,5};
		Story s = new Story(0, story, index);
	
		System.out.print(s.getSolvedStory());
		
		assertTrue(s.getSolvedStory().equals(story));
	}
	
}