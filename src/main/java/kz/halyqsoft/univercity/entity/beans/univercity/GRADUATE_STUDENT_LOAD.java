package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Mar 14, 2017 4:00:04 PM
 */
@Entity
public class GRADUATE_STUDENT_LOAD extends AbstractEntity {

	private static final long serialVersionUID = -7109575727295787705L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID", nullable = false)})
    private EMPLOYEE teacher;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID", nullable = false)})
    private LEVEL level;
    
	@FieldInfo(type = EFieldType.INTEGER, order = 3, min = 1)
    @Column(name = "STUDENT_COUNT", nullable = false)
	private int studentCount;
	
	public GRADUATE_STUDENT_LOAD() {
	}

	public EMPLOYEE getTeacher() {
		return teacher;
	}

	public void setTeacher(EMPLOYEE teacher) {
		this.teacher = teacher;
	}
	
	public LEVEL getLevel() {
		return level;
	}

	public void setLevel(LEVEL level) {
		this.level = level;
	}

	public int getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(int studentCount) {
		this.studentCount = studentCount;
	}
}
