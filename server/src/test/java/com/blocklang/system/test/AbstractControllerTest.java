package com.blocklang.system.test;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.blocklang.system.service.JwtService;
import com.blocklang.system.service.UserService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class AbstractControllerTest extends AbstractSpringTest {

	@MockBean
	protected UserService userService;
	@MockBean
	protected JwtService jwtService;
	
	@Autowired
	private MockMvc mvc;
	
	@BeforeEach
	public void setUp() {
		RestAssuredMockMvc.mockMvc(mvc);
	}
	
}
