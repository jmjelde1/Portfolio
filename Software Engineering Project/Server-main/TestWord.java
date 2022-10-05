/*
 * Test file for Word class.
 */
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestWord {

	/*
	 * Tests the constructor 
	 */
	@Test
	public void testConstructor() {
		Word test = new Word("hello");
		Word test2 = new Word("test");
		Word test3 = new Word("alphabetical");
		Word test4 = new Word("goodbye");
	}
	
	/*
	 * Test the getter method getOriginalWord
	 */
	@Test
	public void testOriginal() {
		Word test = new Word("hello");
		Word test2 = new Word("test");
		Word test3 = new Word("alphabetical");
		Word test4 = new Word("goodbye");
		
		assertEquals(true, test.getOriginalWord().equals("hello"));
		assertEquals(true, test2.getOriginalWord().equals("test"));
		assertEquals(true, test3.getOriginalWord().equals("alphabetical"));
		assertEquals(true, test4.getOriginalWord().equals("goodbye"));
	}
	
	/*
	 * Tests the scramble method and the getter method getScrambledWord
	 */
	@Test
	public void testScramble() {
		Word test = new Word("hello");
		Word test2 = new Word("test");
		Word test3 = new Word("alphabetical");
		Word test4 = new Word("goodbye");
		
		assertEquals(false, test.getScrambledWord().equals("hello"));
		assertEquals(false, test2.getScrambledWord().equals("test"));
		assertEquals(false, test3.getScrambledWord().equals("alphabetical"));
		assertEquals(false, test4.getScrambledWord().equals("goodbye"));	
	}
	
	@Test
	public void testScoreSolution()  {
		Word test = new Word("hello");
		
		assertTrue(5, test.scoreSolution("hello"));
		assertFalse(0, test.scoreSolution("elhlo"));
		assertFalse(0, test.scoreSolution("heoll"));
		assertFalse(0, test.scoreSolution("elohl"));
		assertFalse(0, test.scoreSolution("helol"));
	}

}
