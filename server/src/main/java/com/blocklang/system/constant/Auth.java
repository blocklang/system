package com.blocklang.system.constant;

public abstract class Auth {

	/**************************以下是程序模块中的操作***************************/
	
	// 此处只处理模块和功能访问权限，有关数据过滤的权限不在菜单访问权限里，此处只处理模块和功能访问权限，有关数据权限需要另行处理。。
	/**
	 * 查询多条记录，常用于分页查询
	 */
	private static final String OP_LIST = "list";
	
	/**
	 * 查询一条记录
	 */
	private static final String OP_QUERY = "query";
	
	/**
	 * 新增
	 */
	private static final String OP_NEW = "new";
	
	/**
	 * 修改
	 */
	private static final String OP_EDIT = "edit";
	
	/**
	 * 删除
	 */
	private static final String OP_REMOVE = "remove";
	
	// APP 管理
	// App 标识/系统管理功能模块标识/程序模块标识
	public static final String SYSTEM_APP = "sys/sys/app";
	public static final String SYSTEM_APP_LIST = SYSTEM_APP + "/" + OP_LIST;
	public static final String SYSTEM_APP_QUERY = SYSTEM_APP + "/" + OP_QUERY;
	public static final String SYSTEM_APP_NEW = SYSTEM_APP + "/" + OP_NEW;
	public static final String SYSTEM_APP_EDIT = SYSTEM_APP + "/" + OP_EDIT;
	public static final String SYSTEM_APP_REMOVE = SYSTEM_APP + "/" + OP_REMOVE;
	
	// 用户管理
	public static final String SYSTEM_USER = "sys/sys/user";
	public static final String SYSTEM_USER_LIST = SYSTEM_USER + "/" + OP_LIST;
	public static final String SYSTEM_USER_QUERY = SYSTEM_USER + "/" + OP_QUERY;
	public static final String SYSTEM_USER_NEW = SYSTEM_USER + "/" + OP_NEW;
	public static final String SYSTEM_USER_EDIT = SYSTEM_USER + "/" + OP_EDIT;
	public static final String SYSTEM_USER_REMOVE = SYSTEM_USER + "/" + OP_REMOVE;
	
	// 角色管理
	public static final String SYSTEM_ROLE = "sys/sys/role";
	public static final String SYSTEM_ROLE_LIST = SYSTEM_ROLE + "/" + OP_LIST;
	public static final String SYSTEM_ROLE_QUERY = SYSTEM_ROLE + "/" + OP_QUERY;
	public static final String SYSTEM_ROLE_NEW = SYSTEM_ROLE + "/" + OP_NEW;
	public static final String SYSTEM_ROLE_EDIT = SYSTEM_ROLE + "/" + OP_EDIT;
	public static final String SYSTEM_ROLE_REMOVE = SYSTEM_ROLE + "/" + OP_REMOVE;
	public static final String SYSTEM_ROLE_USERS = SYSTEM_ROLE + "/" + "authUser";
	public static final String SYSTEM_ROLE_RESOURCES = SYSTEM_ROLE + "/" + "authResource";
	
	// 部门管理
	public static final String SYSTEM_DEPT = "sys/sys/dept";
	public static final String SYSTEM_DEPT_LIST = SYSTEM_DEPT + "/" + OP_LIST;
	public static final String SYSTEM_DEPT_QUERY = SYSTEM_DEPT + "/" + OP_QUERY;
	public static final String SYSTEM_DEPT_NEW = SYSTEM_DEPT + "/" + OP_NEW;
	public static final String SYSTEM_DEPT_EDIT = SYSTEM_DEPT + "/" + OP_EDIT;
	public static final String SYSTEM_DEPT_REMOVE = SYSTEM_DEPT + "/" + OP_REMOVE;
	
	// 资源管理
	public static final String SYSTEM_RES = "sys/sys/res";
	public static final String SYSTEM_RES_LIST = SYSTEM_RES + "/" + OP_LIST;
	public static final String SYSTEM_RES_QUERY = SYSTEM_RES + "/" + OP_QUERY;
	public static final String SYSTEM_RES_NEW = SYSTEM_RES + "/" + OP_NEW;
	public static final String SYSTEM_RES_EDIT = SYSTEM_RES + "/" + OP_EDIT;
	public static final String SYSTEM_RES_REMOVE = SYSTEM_RES + "/" + OP_REMOVE;
}