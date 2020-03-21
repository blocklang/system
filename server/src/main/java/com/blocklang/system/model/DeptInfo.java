package com.blocklang.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.blocklang.system.constant.Tree;

@Entity
@Table(name = "sys_dept")
public class DeptInfo extends PartialOperateFields{
	
	private static final long serialVersionUID = 218202018871608914L;

	@Column(name = "dept_name", nullable = false, length = 64)
	private String name;
	
	@Column(name = "parent_id", nullable = false, length = 32)
	private String parentId = Tree.ROOT_PARENT_ID;
	
	@Column(name = "seq")
	private Integer seq = 1;

	@Column(name = "active")
	private Boolean active = true;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
}