package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created 06 ���. 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity
public class TESTING_SUBJECT extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 1)
	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;
	
	public TESTING_SUBJECT() {
	}
	
	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	
	@Override
	public String toString() {
		return subjectName;
	}

}
