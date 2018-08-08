package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class QUALIFICATION extends AbstractEntity {

	private static final long serialVersionUID = 3284500069342008188L;

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "QUALIFICATION_NAME", nullable = false)
	private String qualificationName;

	public QUALIFICATION() {
	}

	public String getQualificationName() {
		return qualificationName;
	}

	public void setQualificationName(String qualificationName) {
		this.qualificationName = qualificationName;
	}

	@Override
	public String toString() {
		return qualificationName;
	}
}
