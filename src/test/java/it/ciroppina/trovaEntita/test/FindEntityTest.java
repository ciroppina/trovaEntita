package it.ciroppina.trovaEntita.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.ciroppina.trovaEntita.FindEntity;
import it.ciroppina.trovaEntita.Group;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * this @class is the tester for it.ciroppina.trovaEntita.FindEntity class
 * @author ciroppina
 *
 */
public class FindEntityTest {

	private static final PrintStream console = System.out;
	private static String CAPITALIZED_WORDS = null;
	private static String toTest = null;

	static {
		//final Enumeration<Object> keys = System.getProperties().keys();
		// while (keys.hasMoreElements()) {
		// String key = (String) keys.nextElement();
		// console.println(key + ": " + System.getProperty(key));
		// }
	}

	@Before
	public void setUp() throws Exception {
		String whereIAm = System.getProperty("user.dir");
		String regexFile = whereIAm + "/src/main/resources/regex.txt";
		//debug: console.println("File of regex(es): " + regexFile);

		Properties regex = new Properties();
		regex.load(new FileInputStream(new File(regexFile)));
		CAPITALIZED_WORDS = regex.getProperty("REGEX_03");
		//debug: console.println("RegEx under test: " + CAPITALIZED_WORDS);

		FileInputStream in  = new FileInputStream( 
			new File(whereIAm + "/src/test/resources/64981.txt")); //194.txt
		byte[] b = new byte[in.available()]; in.read(b);
		toTest = new String(b, StandardCharsets.UTF_8);
		//debug: console.println("Text under test: " + toTest);
		in.close(); in = null;
	}

	@After
	public void tearDown() throws Exception {
		CAPITALIZED_WORDS = null;
		toTest = null;
		System.gc();
	}

	@Test
	public void invalidRegEx_Test() {
		try {
			assertFalse( new FindEntity().isValidRegEx(";))") );
		} catch (AssertionError e) {
			fail("Not a valid regular expression: " + ";)) - looks like an emoticon");
		}
	}

	@Test
	public void regExUnderTestIsValid_Test() {
		assertTrue("Should be a valid regular expression", 
			new FindEntity().isValidRegEx(CAPITALIZED_WORDS));
	}

	@Test
	public void textUnderTestIsNotEmpty_Test() {
		try {
			assertTrue("Should be a non-empty text",
				toTest != null && toTest.length() > 10);
		} catch (NullPointerException e) {
			fail("this Text is not valid: " + null);
		}
	}
	
	@Test
	public void howManyMatches_Test() {
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS);
		if (finder.isValidRegEx(CAPITALIZED_WORDS)) {
			long hm = finder.howManyMatchesFindsInto(toTest);
			assertTrue("Should match more than once", hm > 1);
		} else {
			fail("Invalid regEx! Check it: " + CAPITALIZED_WORDS);
			console.println("Invalid regEx! Check it: " + CAPITALIZED_WORDS);
		}
	}

	@Test
	public void howManyPerMatch_Test() {
		console.println("\nOnly groups that match more than 0 times");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS);
		Map<String, Long> results = finder.countHowManyPerMatchInto(toTest);
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Counted as having matched more than 0 times: " + results.get(k), results.get(k) > 0L);
			console.println(k +" (" + results.get(k) + " times)");
		}
		console.println("Counted " + results.size() + " groups matching more than 0 times");
	}	

	@Test
	public void minFrequency10_Test() {
		Long frequency = 10L;
		console.println("\nOnly groups that match more than 9 times");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS, toTest);
		Map<String, Long> results = finder.matchMinFrequencyOf(frequency);
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Matched the regEx 10 or more times: " + results.get(k), 
					   results.get(k) >= frequency);
			console.println(k +" (" + results.get(k) + " times)");
		}
	}	

	@Test
	public void minFrequency25_Test() {
		Long frequency = 25L;
		console.println("\nOnly groups that match more than 24 times");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS, toTest);
		Map<String, Long> results = finder.matchMinFrequencyOf(frequency);
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Matched the regEx 25 or more times: " + results.get(k), 
					   results.get(k) >= frequency);
			console.println(k +" (" + results.get(k) + " times)");
		}
	}
	
	@Test
	public void countGroups_Test() {
		console.println("\nReturns Group-objects that match more than 0 times");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS);
		Map<String, Group> results = finder.createGroupsFrom(toTest);
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Group-objects that matched the regEx more than 0 times: " + results.get(k), results.get(k).getCount() > 0L);
			console.println(k +" (" + results.get(k).getCount() + " times)");
		}
		Long expected = (long) finder.countHowManyPerMatchInto(toTest).size();
		assertTrue("Nr-Of-Matches SHOULD MATCH TO Nr-Of-Groups", expected == results.size());
	}
	
	@Test
	public void populateGroupsWithOccurrences_Test() {
		console.println("\nReturns Group-objects that match more than 0 times, with occurrences' offsets");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS);
		Map<String, Group> results = finder.storeGroupsOccurrencesFoundIn(toTest);
		assertTrue("Nr-Of-Matches SHOULD MATCH TO Nr-Of-Groups", 
				finder.createGroupsFrom(toTest).size() == results.size());
		
		//print occurrences' offsets
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			console.println(k +" (" + results.get(k).getCount() + " times)");
			List<String> offsets = results.get(k).getOffsetList();
			assertTrue("The list of occurrences must have 1 offsets-couple, at least",
					   offsets.size() > 0L);
			//debug: console.println("the NUMBER of offsets-couples is: " + offsets.size() );
			for (String couple : offsets) {
				console.println("\t"+couple);
			}
		}
	}

	@Test
	public void minFrequency25Groups_Test() {
		Long frequency = 25L;
		console.println("\nOnly Group-objects that match more than 24 times");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS, toTest);
		Map<String, Group> results = finder.groupWithMinFrequencyOf(frequency);
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Only Group-objects that match more than 24 times, counted: " 
			           + results.get(k), results.get(k).getCount() >= frequency);
			console.println(k +" (" + results.get(k).getCount() + " times)");
		}
	}

	@Test
	public void minFrequency10Groups_Test() {
		Long frequency = 10L;
		console.println("\nOnly Group-objects that match more than 9 times");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS, toTest);
		Map<String, Group> results = finder.groupWithMinFrequencyOf(frequency);
		Iterator<String> iterator = results.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Only Group-objects that match more than 9 times, counted: " 
				       + results.get(k), results.get(k).getCount() >= frequency);
			console.println(k +" (" + results.get(k).getCount() + " times)");
		}
	}
	
	@Test
	public void onlyPeople_frequency_Test() {
		Long frequency = 10L;
		console.println("\nAll Group-objects that match at least 10 times AND looks like PEOPLE");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS, toTest);

		Long start = System.currentTimeMillis();
		Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
		Map<String, Group> people = finder.people(groups);
		Iterator<String> iterator = people.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Only Group-objects that match min. "+ frequency+" times, counted: " 
				+ people.get(k), people.get(k).getCount() >= frequency);
			console.println(k +" (" + people.get(k).getCount() + " times) is a: " + people.get(k).getMainQualifier());
			assertTrue("Should only be PERSON", people.get(k).getMainQualifier().equals("PERSON"));
		}
		console.println("TO QUALIFY PEOPLE IT TOOK: " 
				+ ((System.currentTimeMillis() - start) / 1000.0) + " seconds");

		//assertTrue(true);
	}

	@Test
	public void everyQualifier_frequency_Test() {
		Long frequency = 10L;
		console.println("\nAll Group-objects that match at least 10 times AND qualify everything");
		FindEntity finder = new FindEntity(CAPITALIZED_WORDS, toTest);

		Long start = System.currentTimeMillis();
		Map<String, Group> groups = finder.groupWithMinFrequencyOf(frequency);
		Map<String, Group> every = finder.everyQualifier(groups);
		Iterator<String> iterator = every.keySet().iterator();
		while (iterator.hasNext()) {
			String k = iterator.next();
			assertTrue("Only Group-objects that match min. "+ frequency+" times, counted: " 
				+ every.get(k), every.get(k).getCount() >= frequency);
			console.println(k +" (" + every.get(k).getCount() + " times) is a: " 
				            + every.get(k).getMainQualifier());
			assertTrue("Should be every Qualifier, not only PERSON", 
					   every.get(k).getMainQualifier().length() >= 1L);
		}
		console.println("TO QUALIFY ENTITIES IT TOOK: " 
				+ ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
	}
}
