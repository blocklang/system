package com.blocklang.system.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blocklang.system.model.AppInfo;

public interface AppDao extends JpaRepository<AppInfo, String>{

}
