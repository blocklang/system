package com.blocklang.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.RoleInfo;

public interface RoleDao extends JpaRepository<RoleInfo, String>{

}
