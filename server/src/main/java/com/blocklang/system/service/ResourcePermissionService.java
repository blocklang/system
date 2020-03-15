package com.blocklang.system.service;

import java.util.Optional;

/**
 * 资源分为功能模块、程序模块和操作按钮等，本服务用于对程序模块和操作按钮进行权限控制。
 * 
 * @author Zhengwei Jin
 *
 */
public interface ResourcePermissionService {

	/**
	 * 判断登录用户是否有权对资源执行某一项操作
	 * 
	 * @param userId        登录用户标识
	 * @param resourceId    资源标识
	 * @param auth          权限标签，用一个自定义的字符串表示一个程序模块中的权限，常用标签有 query(查询)，new(新增)，edit(修改)，remove(删除)
	 * @return 如果有权则返回 <code>true</code>；否则返回 <code>false</code>
	 */
	Optional<Boolean> canAccess(String userId, String resourceId, String auth);

}
