package com.blocklang.system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;

import com.blocklang.system.model.DeptInfo;

public interface DeptService {

	Optional<DeptInfo> findById(String deptId);

	Optional<DeptInfo> find(String parentDeptId, String deptName);

	DeptInfo save(DeptInfo dept);

	List<DeptInfo> findChildren(String parentDeptId, Sort sort);

}
