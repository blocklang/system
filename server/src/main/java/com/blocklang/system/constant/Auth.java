package com.blocklang.system.constant;

public abstract class Auth {
	
	/**************************以下是程序模块***************************/
	/**
	 * 程序模块，可以设置为 index，但可以不填写，因为凡是程序模块，都为此值
	 */
	public static String INDEX = "index";

	/**************************以下是程序模块中的操作***************************/
	
	// 此处只处理模块和功能访问权限，有关数据过滤的权限不在菜单访问权限里，此处只处理模块和功能访问权限，有关数据权限需要另行处理。。
	/**
	 * 查询多条记录，常用于分页查询
	 */
	public static String LIST = "list";
	
	/**
	 * 查询一条记录
	 */
	public static String QUERY = "query";
	
	/**
	 * 新增
	 */
	public static String NEW = "new";
	
	/**
	 * 修改
	 */
	public static String EDIT = "edit";
	
	/**
	 * 删除
	 */
	public static String REMOVE = "remove";
}