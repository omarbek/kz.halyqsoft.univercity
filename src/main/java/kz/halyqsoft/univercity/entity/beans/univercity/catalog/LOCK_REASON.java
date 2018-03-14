package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Sep 30, 2016 9:54:36 AM
 */
@Entity
public class LOCK_REASON extends AbstractEntity {

	private static final long serialVersionUID = -2575239430003436413L;

	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 1)
	@Column(name = "REASON", nullable = false)
	private String reason;
	
	@Column(name = "LOCK_TYPE", nullable = false)
	private Integer lockType;
	
	public LOCK_REASON() {
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getLockType() {
		return lockType;
	}

	public void setLockType(Integer lockType) {
		this.lockType = lockType;
	}

	@Override
	public String toString() {
		return reason;
	}
}
