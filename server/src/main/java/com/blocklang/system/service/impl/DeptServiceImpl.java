package com.blocklang.system.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.DeptDao;
import com.blocklang.system.model.DeptInfo;
import com.blocklang.system.service.DeptService;

@Service
public class DeptServiceImpl implements DeptService {

	@Autowired
	private DeptDao deptDao;
	
	@Override
	public Optional<DeptInfo> findById(String deptId) {
		return deptDao.findById(deptId);
	}

	@Override
	public Optional<DeptInfo> find(String parentDeptId, String deptName) {
		return deptDao.findByParentIdAndName(parentDeptId, deptName);
	}

	@Override
	public DeptInfo save(DeptInfo dept) {
		return deptDao.save(dept);
	}

	@Override
	public List<DeptInfo> findChildren(String parentDeptId, Sort sort) {
		return deptDao.findAllByParentId(parentDeptId, sort);
	}

}
