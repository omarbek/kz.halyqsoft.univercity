package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Oct 27, 2015 12:03:28 PM
 */
@MappedSuperclass
public abstract class AbstractTypeEntity extends AbstractEntity {

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "TYPE_NAME", nullable = false)
	private String typeName;
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	@Override
	public String toString() {
		return typeName;
	}
}
