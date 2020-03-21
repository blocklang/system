package com.blocklang.system.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;

import com.blocklang.system.dao.DeptDao;
import com.blocklang.system.model.DeptInfo;
import com.blocklang.system.service.DeptService;
import com.blocklang.system.test.AbstractServiceTest;

public class DeptServiceImplTest extends AbstractServiceTest{

	@Autowired
	private DeptService deptService;
	@Autowired
	private DeptDao deptDao;
	
	@Test
	public void save_app_id_parent_id_name_duplicated() {
		String parentId = "2";
		String name = "3";
		
		DeptInfo dept1 = new DeptInfo();
		dept1.setId("1");
		dept1.setParentId(parentId);
		dept1.setName(name);
		dept1.setCreateTime(LocalDateTime.now());
		dept1.setCreateUserId("1");
		deptService.save(dept1);
		
		DeptInfo dept2 = new DeptInfo();
		dept2.setId("2");
		dept2.setParentId(parentId);
		dept2.setName(name);
		dept2.setCreateTime(LocalDateTime.now());
		dept2.setCreateUserId("1");
		deptService.save(dept2);
		
		assertThrows(DataIntegrityViolationException.class, ()-> {
			deptDao.flush();
		});
	}
	
	@Test
	public void findById() {
		String deptId = "1";
		String parentId = "2";
		String name = "3";
		
		DeptInfo dept1 = new DeptInfo();
		dept1.setId(deptId);
		dept1.setParentId(parentId);
		dept1.setName(name);
		dept1.setCreateTime(LocalDateTime.now());
		dept1.setCreateUserId("1");
		deptService.save(dept1);
		
		assertThat(deptService.findById(deptId)).isPresent();
		assertThat(deptService.findById("2")).isEmpty();
	}
	
	@Test
	public void find() {
		String deptId = "1";
		String parentId = "2";
		String name = "3";
		
		DeptInfo dept1 = new DeptInfo();
		dept1.setId(deptId);
		dept1.setParentId(parentId);
		dept1.setName(name);
		dept1.setCreateTime(LocalDateTime.now());
		dept1.setCreateUserId("1");
		deptService.save(dept1);
		
		assertThat(deptService.find(parentId, name)).isPresent();
		assertThat(deptService.find(parentId, "not-exist")).isEmpty();
	}
	
	@Test
	public void findChildren() {
		String parentId = "2";
		
		DeptInfo dept1 = new DeptInfo();
		dept1.setId("1");
		dept1.setParentId(parentId);
		dept1.setName("name1");
		dept1.setCreateTime(LocalDateTime.now());
		dept1.setCreateUserId("1");
		deptService.save(dept1);
		
		DeptInfo dept2 = new DeptInfo();
		dept2.setId("2");
		dept2.setParentId(parentId);
		dept2.setName("name2");
		dept2.setCreateTime(LocalDateTime.now());
		dept2.setCreateUserId("1");
		deptService.save(dept2);
		
		Sort sort = Sort.by("name").descending();
		assertThat(deptService.findChildren(parentId, sort)).hasSize(2).first().isEqualToComparingOnlyGivenFields(dept2, "name");
		assertThat(deptService.findChildren("no-data", null)).isEmpty();
	}
}
