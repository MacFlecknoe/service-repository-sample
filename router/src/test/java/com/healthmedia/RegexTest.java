package com.healthmedia;

import static org.junit.Assert.*;

import org.junit.Test;

public class RegexTest {

	@Test
	public void test() {
		
		String test = "\"hello.dd\"";
		
		System.out.println(test);
		
		//System.out.println(test.replaceAll("\"[\\w][\\w]+\"", "_"));
		System.out.println(test.replaceAll("\"([A-Za-z][\\w]+)\\.(\\w)+\"", "$1_$2"));
		
		fail("Not yet implemented");
	}

}
