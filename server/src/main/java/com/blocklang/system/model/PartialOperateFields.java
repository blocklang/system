package com.blocklang.system.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 数据库操作字段
 * 
 * @author Zhengwei Jin
 *
 */
@MappedSuperclass
public abstract class PartialOperateFields extends PartialIdField{

	private static final long serialVersionUID = -3938378268785302333L;

	@Column(name = "create_user_id", insertable = true, updatable = false, nullable = false)
	private String createUserId;
	
	@Column(name = "create_time", insertable = true, updatable = false, nullable = false)
	private LocalDateTime createTime;

	@Column(name = "last_update_user_id")
	private String lastUpdateUserId;
	
	@Column(name = "last_update_time" )
	private LocalDateTime lastUpdateTime;

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public String getLastUpdateUserId() {
		return lastUpdateUserId;
	}

	public void setLastUpdateUserId(String lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}

	public LocalDateTime getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(LocalDateTime lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
