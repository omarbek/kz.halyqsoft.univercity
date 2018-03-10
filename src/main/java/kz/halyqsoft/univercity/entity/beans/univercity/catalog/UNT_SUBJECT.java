package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Oct 27, 2015 12:26:38 PM
 */
@Entity
public class UNT_SUBJECT extends AbstractEntity {

	private static final long serialVersionUID = -4711930810481376584L;

	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 1)
	@Column(name = "SUBJECT_NAME", nullable = false)
	private String subjectName;

	public UNT_SUBJECT() {
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
