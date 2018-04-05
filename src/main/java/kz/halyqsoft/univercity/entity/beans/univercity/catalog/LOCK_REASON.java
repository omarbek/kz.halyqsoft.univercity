package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

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

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "USER_TYPE_ID", referencedColumnName = "ID")})
	private USER_TYPE userType;

	public LOCK_REASON() {
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public USER_TYPE getUserType() {
		return userType;
	}

	public void setUserType(USER_TYPE userType) {
		this.userType = userType;
	}

	@Override
	public String toString() {
		return reason;
	}
}
