package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Rakymzhan A. Kenzhegul
 * @created Jun 15, 2016 11:47:26 AM
 */
@Entity
public class LESSON_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = 8727135395953597153L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "LESSON_ID", referencedColumnName = "ID")})
    private LESSON lesson;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT_EDUCATION studentEducation;
    
	@FieldInfo(type = EFieldType.INTEGER, min = 1, max = 3, order = 4)
	@Column(name = "ATTENDANCE_MARK", nullable = false)
    private Integer attendanceMark;
	
    @FieldInfo(type = EFieldType.DOUBLE, min = 0, max = 99, order = 4)
	@Column(name = "GRADE", nullable = false)
	private Double grade;
    
    @FieldInfo(type = EFieldType.TEXT, max = 256, order = 5)
	@Column(name = "COMMENTS")
	private String comments;
    
    @FieldInfo(type = EFieldType.DATETIME, order = 6)
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
    
    @FieldInfo(type = EFieldType.TEXT, max = 32, order = 7)
	@Column(name = "UPDATED_BY")
	private String updatedBy;
    
    @Column(name = "OLD_ID")
	private Integer oldId;
    
	public LESSON_DETAIL() {
	}

	public LESSON getLesson() {
		return lesson;
	}

	public void setLesson(LESSON lesson) {
		this.lesson = lesson;
	}

	public STUDENT_EDUCATION getStudentEducation() {
		return studentEducation;
	}

	public void setStudentEducation(STUDENT_EDUCATION studentEducation) {
		this.studentEducation = studentEducation;
	}

	public Integer getAttendanceMark() {
		return attendanceMark;
	}

	public void setAttendanceMark(Integer attendanceMark) {
		this.attendanceMark = attendanceMark;
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

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Integer getOldId() {
		return oldId;
	}

	public void setOldId(Integer oldId) {
		this.oldId = oldId;
	}
}
