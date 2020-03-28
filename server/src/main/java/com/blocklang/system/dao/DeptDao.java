package com.blocklang.system.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.DeptInfo;

public interface DeptDao extends JpaRepository<DeptInfo, String>{

	Optional<DeptInfo> findByParentIdAndName(String parentDeptId, String deptName);

	List<DeptInfo> findAllByParentId(String parentDeptId, Sort sort);

	Integer countByParentId(String parentDeptId);
}
