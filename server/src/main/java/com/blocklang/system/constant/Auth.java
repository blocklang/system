package com.blocklang.system.constant;

public abstract class Auth {

	/**
	 * 查询，查询表中所有数据，即包括查数据列表，也包括查一条记录。
	 * 
	 * 此处只处理模块和功能访问权限，有关数据权限需要另行处理。
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