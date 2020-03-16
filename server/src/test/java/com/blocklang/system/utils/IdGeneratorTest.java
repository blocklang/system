package com.blocklang.system.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class IdGeneratorTest {

	@Test
	public void uuid() {
		assertThat(IdGenerator.uuid()).doesNotContain("-");
	}
}
