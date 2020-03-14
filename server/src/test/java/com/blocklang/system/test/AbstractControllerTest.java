package com.blocklang.system.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class AbstractControllerTest extends AbstractSpringTest {

	@Autowired
	private MockMvc mvc;
	
	@BeforeEach
	public void setUp() {
		RestAssuredMockMvc.mockMvc(mvc);
	}
	
}
