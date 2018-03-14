package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author Omarbek
 * Created Oct 27, 2015 12:05:45 PM
 */
@MappedSuperclass
public abstract class AbstractStatusEntity extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "STATUS_NAME", nullable = false)
	private String statusName;
	
	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	@Override
	public String toString() {
		return statusName;
	}
}
