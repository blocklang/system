package com.blocklang.system.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blocklang.system.dao.AppDao;
import com.blocklang.system.model.AppInfo;
import com.blocklang.system.service.AppService;

@Service
public class AppServiceImpl implements AppService {

	@Autowired
	private AppDao appDao;
	
	@Override
	public AppInfo save(AppInfo appInfo) {
		return appDao.save(appInfo);
	}

	@Override
	public Optional<AppInfo> findById(String id) {
		return appDao.findById(id);
	}

	@Override
	public Page<AppInfo> findAll(Pageable pageable) {
		return appDao.findAll(pageable);
	}

	@Override
	public Optional<AppInfo> find(String createUserId, String appName) {
		return appDao.findByCreateUserIdAndName(createUserId, appName);
	}

}
