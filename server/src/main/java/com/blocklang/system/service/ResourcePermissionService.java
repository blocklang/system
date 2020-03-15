package com.blocklang.system.service;

import java.util.Optional;

import com.blocklang.system.constant.Auth;
import com.blocklang.system.constant.ResourceType;

/**
 * 资源分为功能模块、程序模块和操作按钮等，本服务用于对程序模块和操作按钮进行权限控制。
 * 
 * @author Zhengwei Jin
 *
 */
public interface ResourcePermissionService {

	/**
	 * 操作按钮级（或方法级）的权限判断，判断登录用户是否有权对资源执行某一项操作。
	 * 
	 * @param userId        登录用户标识
	 * @param resourceId    资源标识，<strong>专指程序模块，即 resourceType 的值为 {@link ResourceType#PROGRAM}</strong>
	 * @param auth          操作按钮的权限标签（不能是程序模块的权限 {@link Auth#INDEX}），用一个自定义的字符串表示一个程序模块中的权限，一些常用操作存在 {@link Auth} 常量类中。
	 * @return 如果有权访问则返回 <code>true</code>；否则返回 <code>false</code>。
	 */
	Optional<Boolean> canExecute(String userId, String resourceId, String auth);

}