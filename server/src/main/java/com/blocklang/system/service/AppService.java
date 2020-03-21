package com.blocklang.system.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.blocklang.system.model.AppInfo;

public interface AppService {

	AppInfo save(AppInfo appInfo);

	Optional<AppInfo> findById(String id);

	Page<AppInfo> findAll(Pageable pageable);

	Optional<AppInfo> find(String createUserId, String appName);

}
