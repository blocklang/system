package com.blocklang.system.service;

import java.util.List;
import java.util.Optional;

import com.blocklang.system.model.AppInfo;

public interface AppService {

	AppInfo save(AppInfo appInfo);

	AppInfo update(AppInfo appInfo);
	
	Optional<AppInfo> findById(String id);

	List<AppInfo> findAll();

}
