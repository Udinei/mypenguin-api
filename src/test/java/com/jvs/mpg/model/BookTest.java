package com.jvs.mpg.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookTest {
	
	@Test
	public void test_authors(){ 
		assertEquals("Dan Howell", Book.getAuthorsByBookName("The Amazing Book Is Not on Fire: The World of Dan and Phil")); 
	}


}
