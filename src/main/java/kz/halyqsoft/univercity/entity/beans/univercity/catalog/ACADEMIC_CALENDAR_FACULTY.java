package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Oct 21, 2016 1:07:27 PM
 */
@Entity
public class ACADEMIC_CALENDAR_FACULTY extends AbstractEntity {

	private static final long serialVersionUID = -7583017973119461278L;
	
	@FieldInfo(type = EFieldType.TEXT, max = 256)
	@Column(name = "FACULTY_NAME", nullable = false)
	private String facultyName;
		
	public ACADEMIC_CALENDAR_FACULTY() {
	}

	public String getFacultyName() {
		return facultyName;
	}

	public void setFacultyName(String facultyName) {
		this.facultyName = facultyName;
	}

	@Override
	public String toString() {
		return facultyName;
	}
}
