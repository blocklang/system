package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.blocklang.system.service.EncryptService;
import com.blocklang.system.test.AbstractServiceTest;

public class EncryptServiceImplTest extends AbstractServiceTest{

	@Autowired
	private EncryptService encryptService;
	
	@Test
	public void encrypt() {
		assertThat(encryptService.encrypt("a")).isNotEqualTo("a");
	}
	
	@Test
	public void check() {
		assertThat(encryptService.check("a", "a")).isFalse();
		assertThat(encryptService.check("a", "b")).isFalse();
		assertThat(encryptService.check("a", encryptService.encrypt("a"))).isTrue();
	}
}
