package com.blocklang.system.test;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.blocklang.system.SystemApplication;

@SpringBootTest(classes = SystemApplication.class)
@Transactional
public class AbstractDaoTest extends AbstractSpringTest {

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected int countRowsInTable(String tableName) {
		return JdbcTestUtils.countRowsInTable(jdbcTemplate, tableName);
	}
}
