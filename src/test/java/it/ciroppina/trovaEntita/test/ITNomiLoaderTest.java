package it.ciroppina.trovaEntita.test;

import static org.junit.Assert.*;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ITNomiLoaderTest {

	static PrintStream console = null;

	@Before
	public void setUp() throws Exception {
		console = System.out;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void search_NOMI_AsString_test() {
		Long start = System.currentTimeMillis();
		assertTrue(
				ITNomiLoader.NOMI.toUpperCase().contains("GIGIO")
		);
		console.print(System.currentTimeMillis() - start);
	}
	
	@Test
	public void splitAndSearch_NOMI_AsString_test() {
		Long start = System.currentTimeMillis();
		assertTrue(
				ITNomiLoader.splitAndSearch("Il TOPO Presidente dellla di Repubblica Gigio Napolitano")
		);
		console.print(System.currentTimeMillis() - start);
	}

}
