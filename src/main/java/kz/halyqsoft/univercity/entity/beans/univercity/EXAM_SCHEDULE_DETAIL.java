package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jun 28, 2016 4:56:45 PM
 */
@Entity
public class EXAM_SCHEDULE_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -28384132766674780L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EXAM_SCHEDULE_ID", referencedColumnName = "ID")})
    private EXAM_SCHEDULE examSchedule;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
	private STUDENT_EDUCATION studentEducation;
	
	public EXAM_SCHEDULE_DETAIL() {
	}

	public EXAM_SCHEDULE getExamSchedule() {
		return examSchedule;
	}

	public void setExamSchedule(EXAM_SCHEDULE examSchedule) {
		this.examSchedule = examSchedule;
	}

	public STUDENT_EDUCATION getStudentEducation() {
		return studentEducation;
	}

	public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
		this.studentEducation = studentEducation;
	}
}
