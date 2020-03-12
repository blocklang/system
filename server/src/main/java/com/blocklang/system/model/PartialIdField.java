package com.blocklang.system.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 数据库代理主键字段
 * 
 * @author Zhengwei Jin
 *
 */
@MappedSuperclass
public abstract class PartialIdField implements Serializable{

	private static final long serialVersionUID = -880253821847574972L;

	@Id
	@Column(name = "dbid", length=32, updatable = false)
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
