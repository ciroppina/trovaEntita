package it.ciroppina.trovaEntita;

import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * this @class is a entity finder (Capitalized Words) extracted from a text, by using a regEx
 * @author ciroppina
 *
 */
public class FindEntity {
	
	private static PrintStream console = System.out;
	private String regEx = "";
	private String text  = "";
	
	/**
	 * Constructors with various number of @params
	 * @param aRegEx: the current regEx, shold be a valid one
	 * @param aText: the text to parse
	 * @param aConsole: a PrintStream - like System.out
	 */
	public FindEntity(String aRegEx, String aText, PrintStream aConsole) {
		console = aConsole;
		this.regEx = aRegEx;
		this.text = aText; 
	}

	public FindEntity(String aRegEx, String aText) {
		this.regEx = aRegEx;
		this.text = aText; 
	}

	public FindEntity(String aRegEx) {
		this.regEx = aRegEx;
	}

	public FindEntity() {
		super();
	}

	/**
	 * @return if the passed regEx is a valid/compiled one
	 * @param aRegEx: the regex to be validated
	 */
	public boolean isValidRegEx(String aRegEx) {
		try {
			Pattern p = Pattern.compile(aRegEx);
			return p != null;
		} catch (PatternSyntaxException e) {
			console.println("\nFindEntity - not a valid regular expression: " + aRegEx);
			return false;
		}
	}

	/**
	 * @return the long number of matches of the passed regex in the passed text
	 * @param aRexEx: a Regular Expression
	 * @param aText:  a Text to find the regEx into
	 */
	public long howManyMatchesFindsInto(String aText) {
		Matcher pm = createFor(aText);
		long hm = 0L;
		while (pm.find()) { hm++; }
		console.println("FindEntity - found as many as: " 
			+ hm + " matches for this pattern: " + this.regEx);
		
		return hm;
	}
	
	public void consoleThis(String theText) {
		try {
			console.println(theText);	
		} catch (Exception e) {
			console.println(e.getLocalizedMessage());
		}
	}


	/**
	 * counts only the occurrences the reGex matches of every group
	 * when they are > of frequency threshold
	 * @param toTest: the text to parse with the regEx
	 * @return results: the descending-sorted map of matching groups
	 */
	public Map<String, Long> matchMinFrequencyOf(Long frequency) {
		Map<String, Long> counted = countHowManyPerMatchInto(this.text);
		Map<String, Long> results = new HashMap<String, Long>();
		Iterator<String> keys = counted.keySet().iterator();
		while (keys.hasNext()) {
			String k = keys.next();
			if (counted.get(k) > frequency) results.put(k, counted.get(k));
		}
		return sortByValue(results);
	}

	/**
	 * counts how many occurrence the reGex matches of every group
	 * @param toTest: the text to parse with the regEx
	 * @return results: the descending-sorted map of matching groups
	 */
	public Map<String, Long> countHowManyPerMatchInto(String toTest) {
		Map<String, Long> results = new HashMap<String, Long>();
		if (! isValidRegEx(this.regEx)) {
			results.put("NOT A VALID REGEX: " + this.regEx, new Long(0L));
			return results;
		}
		Matcher pm = createFor(toTest);
		while (pm.find()) { 
			String k = pm.group(0).trim();
			k= k.trim().toUpperCase().replace("\r\n", " ").replace("  ", " ");
			if (results.get(k) == null) {
				results.put(k, 1L);
			} else {
				results.put(k, Long.parseLong(""+results.get(k)) + 1L);
			}
		}
		console.println("FindEntity - found: " 
			+ results.size() + " groups (matches) for this pattern: " + this.regEx);
		
		return sortByValue(results);
	}

	/**
	 * counts how many occurrence match the reGex and stores the total into Group-objects
	 * @param toTest: the text to parse with the regEx
	 * @return results: the descending-sorted (occurrences) map of matching Group-objects
	 */
	public Map<String, Group> countGroupsInto(String toTest) {
		Map<String, Group> results = new HashMap<String, Group>();
		if (! isValidRegEx(this.regEx)) {
			results.put("NOT A VALID REGEX: " + this.regEx, new Group("NOT A VALID REGEX"+ this.regEx));
			return results;
		}
		Matcher pm = createFor(toTest);
		while (pm.find()) { 
			String k = pm.group(0).trim();
			k= k.trim().toUpperCase().replace("\r\n", " ").replace("  ", " ");
			Group g = new Group(k);
			if (results.get(k) == null) {
				g.add(1L);
				results.put(k, g);
			} else {
				g.add( Long.parseLong(""+results.get(k).getCount()) + 1L );
				results.put(k, g);
			}
		}
		console.println("FindEntity - found: " 
			+ results.size() + " Group-objects that match this pattern: " + this.regEx);
		
		return sortByProperty(results);
	}

	/**
	 * counts the occurences of every string that matches the regEx, and stores their offsets
	 * into the proper Group-object
	 * @param toTest: the text to parse with the regEx
	 * @return results: the (descending) sorted map of all the Group-objects built
	 */
	public Map<String, Group> storeGroupsOccurrencesFoundIn(String toTest) {
		Map<String, Group> results = new HashMap<String, Group>();
		if (! isValidRegEx(this.regEx)) {
			results.put("NOT A VALID REGEX: " + this.regEx, new Group("NOT A VALID REGEX"+ this.regEx));
			return results;
		}
		Matcher pm = createFor(toTest);
		while (pm.find()) { 
			String k = pm.group(0).trim();
			k= k.trim().toUpperCase().replace("\r\n", " ").replace("  ", " ");
			Group g = new Group(k);
			if (results.get(k) != null)
				g = results.get(k);
			g.add(1L);
			g.addOffsets(pm.start(), pm.end());
			results.put(k, g);
		}
		console.println("FindEntity - in-memory stored: " 
				+ results.size() + " Group-objects that match this pattern: " + this.regEx);
		
		return sortByProperty(results);
	}

	/**
	 * @param toTest: the text to parse
	 * @return pm: a (pattern) Matcher
	 */
	private Matcher createFor(String toTest) {
		Pattern p = Pattern.compile(this.regEx);
		Matcher pm = p.matcher(toTest);
		return pm;
	}

	/**
	 * Utilities
	 */
	
	/**
	 * @param map: input Map to be sorted by value
	 * @return a sorted Map
	 */
	static <K, V extends Comparable<? super V>> Map<K, V> 
    sortByValue( Map<K, V> map ) {
	    List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
	        @Override
	        public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	        {
	            return (o2.getValue()).compareTo( o1.getValue() );
	        }
	    } ); //end of sort method
	
	    Map<K, V> result = new LinkedHashMap<>();
	    for (Map.Entry<K, V> entry : list)
	    {
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}

	/**
	 * @param map: input Map to be sorted by Group
	 * @return a sorted Map
	 */
	static <K, V extends Comparable<? super V>> Map<String, Group> 
    sortByProperty( Map<String, Group> map ) {
	    List<Map.Entry<String, Group>> list = new LinkedList<>( map.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<String, Group>>() {
	        @Override
	        public int compare( Map.Entry<String, Group> o1, Map.Entry<String, Group> o2 )
	        {
	            return ( ((Group) o2.getValue()).getCount()) 
	            	.compareTo( ((Group)o1.getValue()).getCount() );
	        }
	    } ); //end of sort method
	
	    Map<String, Group> result = new LinkedHashMap<>();
	    for (Map.Entry<String, Group> entry : list)
	    {
	        result.put( entry.getKey(), entry.getValue() );
	    }
	    return result;
	}

}
