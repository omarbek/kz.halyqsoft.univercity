package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jun 16, 2016 10:54:50 AM
 */
@Entity
public class V_LESSON_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 2430693304768701965L;

	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 1)
	@Column(name = "STUDENT_CODE", nullable = false)
	private String studentCode;
	
	@FieldInfo(type = EFieldType.TEXT, max = 12, order = 2)
	@Column(name = "STUDENT_FIO", nullable = false)
	private String studentFIO;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 3)
	@Column(name = "ATTENDED", nullable = false)
    private boolean attended;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 4)
	@Column(name = "LATE", nullable = false)
    private boolean late;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 5)
	@Column(name = "MISSED", nullable = false)
    private boolean missed;
	
	@FieldInfo(type = EFieldType.DOUBLE, order = 6)
	@Column(name = "GRADE", nullable = false)
	private Double grade;
	
	@FieldInfo(type = EFieldType.TEXT, order = 7)
	@Column(name = "COMMENTS")
	private String comments;
	
	public V_LESSON_DETAIL() {
	}

	public String getStudentCode() {
		return studentCode;
	}

	public void setStudentCode(String studentCode) {
		this.studentCode = studentCode;
	}

	public String getStudentFIO() {
		return studentFIO;
	}

	public void setStudentFIO(String studentFIO) {
		this.studentFIO = studentFIO;
	}

	public boolean isAttended() {
		return attended;
	}

	public void setAttended(boolean attended) {
		this.attended = attended;
	}

	public boolean isLate() {
		return late;
	}

	public void setLate(boolean late) {
		this.late = late;
	}

	public boolean isMissed() {
		return missed;
	}

	public void setMissed(boolean missed) {
		this.missed = missed;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
}
